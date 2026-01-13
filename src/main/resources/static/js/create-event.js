/**
 * create-event.js
 * Логика создания события: поиск, модальные окна, работа с вложенными сущностями.
 */

// === ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ ===
let selectedGames = [];  // Список выбранных игр (существующих и новых)
let selectedThemes = []; // Список выбранных тем
let selectedRules = [];  // Список новых правил
let newPlace = null;     // Объект нового места (если создаем с нуля)

// Временное хранилище жанров для текущей редактируемой игры (в модалке)
let tempGameGenres = [];

document.addEventListener('DOMContentLoaded', () => {
    // Кнопка "Назад"
    document.getElementById('btnBack').addEventListener('click', () => {
        if (confirm("Вы уверены, что хотите вернуться на главный экран? Введенные данные не сохранятся.")) {
            window.location.href = "/";
        }
    });

    // Отправка формы
    document.getElementById('createEventForm').addEventListener('submit', handleFormSubmit);

    // Делаем функции доступными глобально (для onclick в HTML)
    window.openModal = openModal;
    window.closeModal = closeModal;
    window.saveNewPlace = saveNewPlace;
    window.removePlace = removePlace;
    window.searchPlaces = searchPlaces;
    window.saveNewGame = saveNewGame;
    window.removeGame = removeGame;
    window.searchGames = searchGames;
    window.saveNewTheme = saveNewTheme;
    window.removeTheme = removeTheme;
    window.searchThemes = searchThemes;
    window.saveNewRule = saveNewRule;
    window.removeRule = removeRule;
    window.saveNewGenre = saveNewGenre;
    window.removeTempGenre = removeTempGenre;
    window.searchGenres = searchGenres;
});

// === ОБЩИЕ ФУНКЦИИ ===
function openModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

// === МЕСТО ПРОВЕДЕНИЯ ===
function saveNewPlace() {
    const name = document.getElementById('newPlaceName').value.trim();
    if (!name) { alert("Введите название места"); return; }

    newPlace = {
        name: name,
        city: document.getElementById('newPlaceCity').value,
        district: document.getElementById('newPlaceDistrict').value,
        address: document.getElementById('newPlaceAddress').value,
        description: document.getElementById('newPlaceDesc').value,
        additionalInfo: document.getElementById('newPlaceInfo').value
    };

    // Обновляем UI
    document.getElementById('selectedPlaceName').innerText = newPlace.name + " (Новое)";
    document.getElementById('selectedPlace').style.display = 'flex';
    document.getElementById('placeSearch').style.display = 'none';
    document.getElementById('placeId').value = ""; // ID нет, так как новое

    // Очистка и закрытие
    closeModal('placeModal');
}

function removePlace() {
    newPlace = null;
    document.getElementById('placeId').value = "";
    document.getElementById('selectedPlace').style.display = 'none';
    document.getElementById('placeSearch').style.display = 'block';
    document.getElementById('placeSearch').value = "";
}

function selectPlace(place) {
    document.getElementById('placeId').value = place.id;
    document.getElementById('selectedPlaceName').innerText = place.name;
    document.getElementById('selectedPlace').style.display = 'flex';
    document.getElementById('placeSearch').style.display = 'none';
    document.getElementById('placeResults').innerHTML = "";
    newPlace = null;
}

async function searchPlaces(query) {
    const resultsDiv = document.getElementById('placeResults');
    resultsDiv.innerHTML = "";
    if (query.trim().length < 2) return;

    try {
        const response = await fetch(`/api/search/places?query=${encodeURIComponent(query)}`);
        const places = await response.json();

        if (places.length === 0) {
            resultsDiv.innerHTML = "<div class='search-item' style='color:#999'>Ничего не найдено</div>";
            return;
        }

        places.forEach(p => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = p.name;
            div.onclick = () => selectPlace(p);
            resultsDiv.appendChild(div);
        });
    } catch (e) { console.error(e); }
}

// === ИГРЫ ===

function saveNewGame() {
    const name = document.getElementById('newGameName').value.trim();
    if (!name) { alert("Введите название игры"); return; }

    const game = {
        name: name,
        description: document.getElementById('newGameDesc').value,
        maxPlayers: document.getElementById('newGameMax').value,
        time: document.getElementById('newGameTime').value,
        difficulty: document.getElementById('newGameDiff').value, // 'easy', 'medium', 'hard'
        genres: [...tempGameGenres], // Копируем массив жанров
        isNew: true
    };

    selectedGames.push(game);
    renderGameTags();

    // Сброс формы модалки
    document.getElementById('newGameName').value = "";
    document.getElementById('newGameDesc').value = "";
    document.getElementById('newGameMax').value = "";
    document.getElementById('newGameTime').value = "";
    tempGameGenres = [];
    document.getElementById('newGameSelectedGenres').innerHTML = "";

    closeModal('gameModal');
}

function selectGame(game) {
    // Если выбираем существующую, полные данные не нужны, только ID и имя
    const gameObj = { id: game.id, name: game.name, isNew: false };
    if (selectedGames.some(g => g.id === game.id)) return; // Не добавлять дубли

    selectedGames.push(gameObj);
    renderGameTags();

    document.querySelector('input[placeholder="Поиск игры..."]').value = "";
    document.getElementById('gameResults').innerHTML = "";
}

function renderGameTags() {
    const container = document.getElementById('selectedGames');
    container.innerHTML = "";
    selectedGames.forEach((g, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        const label = g.isNew ? `${g.name} (New)` : g.name;
        tag.innerHTML = `${label} <span onclick="removeGame(${index})">&times;</span>`;
        container.appendChild(tag);
    });
}

function removeGame(index) {
    selectedGames.splice(index, 1);
    renderGameTags();
}

async function searchGames(query) {
    const resultsDiv = document.getElementById('gameResults');
    resultsDiv.innerHTML = "";
    if (query.trim().length < 2) return;

    try {
        const response = await fetch(`/api/search/games?query=${encodeURIComponent(query)}`);
        const games = await response.json();
        games.forEach(g => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = g.name;
            div.onclick = () => selectGame(g);
            resultsDiv.appendChild(div);
        });
    } catch (e) { console.error(e); }
}

// === ЖАНРЫ (Внутри создания игры) ===

function saveNewGenre() {
    const name = document.getElementById('newGenreName').value.trim();
    if (!name) return;

    const genre = {
        name: name,
        description: document.getElementById('newGenreDesc').value,
        isNew: true
    };

    addTempGenre(genre);
    document.getElementById('newGenreName').value = "";
    document.getElementById('newGenreDesc').value = "";
    closeModal('genreModal');
}

function addTempGenre(genre) {
    if (tempGameGenres.some(g => g.name === genre.name)) return;
    tempGameGenres.push(genre);
    renderGenreTags();
    document.getElementById('genreResults').innerHTML = "";
    document.querySelector('input[placeholder="Поиск жанра..."]').value = "";
}

function renderGenreTags() {
    const container = document.getElementById('newGameSelectedGenres');
    container.innerHTML = "";
    tempGameGenres.forEach((g, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.innerHTML = `${g.name} <span onclick="removeTempGenre(${index})">&times;</span>`;
        container.appendChild(tag);
    });
}

function removeTempGenre(index) {
    tempGameGenres.splice(index, 1);
    renderGenreTags();
}

// Поиск жанров (РЕАЛЬНЫЙ API)
async function searchGenres(query) {
    const resultsDiv = document.getElementById('genreResults');
    resultsDiv.innerHTML = "";

    if (query.trim().length < 2) return;

    try {
        // Делаем запрос к серверу
        const response = await fetch(`/api/search/genres?query=${encodeURIComponent(query)}`);
        const genres = await response.json();

        if (genres.length === 0) {
            resultsDiv.innerHTML = "<div class='search-item' style='color:#999'>Ничего не найдено</div>";
            return;
        }

        genres.forEach(g => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = g.name;
            // При клике добавляем жанр во временный список
            div.onclick = () => addTempGenre({id: g.id, name: g.name, isNew: false});
            resultsDiv.appendChild(div);
        });
    } catch (e) {
        console.error("Ошибка поиска жанров:", e);
    }
}



// === ТЕМЫ ===
function saveNewTheme() {
    const name = document.getElementById('newThemeName').value.trim();
    if (!name) return;
    const theme = {
        name: name,
        description: document.getElementById('newThemeDesc').value,
        isNew: true
    };
    if (selectedThemes.some(t => t.name === name)) return;

    selectedThemes.push(theme);
    renderThemeTags();
    document.getElementById('newThemeName').value = "";
    document.getElementById('newThemeDesc').value = "";
    closeModal('themeModal');
}

function selectTheme(theme) {
    if (selectedThemes.some(t => t.id === theme.id)) return;
    selectedThemes.push({ id: theme.id, name: theme.name, isNew: false });
    renderThemeTags();
    document.getElementById('themeResults').innerHTML = "";
    document.querySelector('input[placeholder="Поиск темы..."]').value = "";
}

function renderThemeTags() {
    const container = document.getElementById('selectedThemes');
    container.innerHTML = "";
    selectedThemes.forEach((t, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        tag.innerHTML = `${t.name} <span onclick="removeTheme(${index})">&times;</span>`;
        container.appendChild(tag);
    });
}

function removeTheme(index) {
    selectedThemes.splice(index, 1);
    renderThemeTags();
}

async function searchThemes(query) {
    const resultsDiv = document.getElementById('themeResults');
    resultsDiv.innerHTML = "";
    if (query.trim().length < 2) return;

    try {
        const response = await fetch(`/api/search/themes?query=${encodeURIComponent(query)}`);
        const themes = await response.json();
        themes.forEach(t => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = t.name;
            div.onclick = () => selectTheme(t);
            resultsDiv.appendChild(div);
        });
    } catch (e) { console.error(e); }
}

// === ПРАВИЛА ===
function saveNewRule() {
    const desc = document.getElementById('newRuleDesc').value.trim();
    if (!desc) return;

    selectedRules.push({ description: desc, isNew: true });
    renderRuleTags();

    document.getElementById('newRuleDesc').value = "";
    closeModal('ruleModal');
}

function renderRuleTags() {
    const container = document.getElementById('selectedRules');
    container.innerHTML = "";
    selectedRules.forEach((r, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        const text = r.description.length > 25 ? r.description.substring(0, 25) + "..." : r.description;
        tag.innerHTML = `${text} <span onclick="removeRule(${index})">&times;</span>`;
        container.appendChild(tag);
    });
}

function removeRule(index) {
    selectedRules.splice(index, 1);
    renderRuleTags();
}

// === ПОИСК ПРАВИЛ ===

function selectRule(rule) {
    // Проверка на дубликаты
    if (selectedRules.some(r => r.id === rule.id)) return;

    // Добавляем как существующее (isNew = false)
    selectedRules.push({
        id: rule.id,
        description: rule.name, // В API мы маппили description -> name
        isNew: false
    });
    renderRuleTags();

    document.getElementById('ruleResults').innerHTML = "";
    document.querySelector('input[placeholder="Поиск правила..."]').value = "";
}

async function searchRules(query) {
    const resultsDiv = document.getElementById('ruleResults');
    resultsDiv.innerHTML = "";
    if (query.trim().length < 2) return;

    try {
        const response = await fetch(`/api/search/rules?query=${encodeURIComponent(query)}`);
        const rules = await response.json();
        rules.forEach(r => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = r.name; // Это description
            div.onclick = () => selectRule(r);
            resultsDiv.appendChild(div);
        });
    } catch (e) { console.error(e); }
}


// === ОТПРАВКА ФОРМЫ ===
async function handleFormSubmit(e) {
    e.preventDefault();

    const formData = new FormData(e.target);

    // Валидация даты
    const dateStr = formData.get('date');
    if (!dateStr) { alert("Выберите дату"); return; }
    if (new Date(dateStr) < new Date()) { alert("Дата не может быть в прошлом"); return; }

    // Валидация места
    if (!document.getElementById('placeId').value && !newPlace) {
        alert("Выберите место проведения");
        return;
    }

    // Сборка DTO
    const dto = {
        name: formData.get('name'),
        description: formData.get('description'),
        date: dateStr,
        maxPlayers: formData.get('maxPlayers'),

        placeId: document.getElementById('placeId').value || null,
        newPlace: newPlace,

        // Игры: ID существующих + объекты новых
        gameIds: selectedGames.filter(g => !g.isNew).map(g => g.id),
        newGames: selectedGames.filter(g => g.isNew),

        // Темы
        themeIds: selectedThemes.filter(t => !t.isNew).map(t => t.id),
        newThemes: selectedThemes.filter(t => t.isNew),

        // Правила: разделяем на ID и новые
        ruleIds: selectedRules.filter(r => !r.isNew).map(r => r.id),
        newRules: selectedRules.filter(r => r.isNew),
    };

    try {
        const response = await fetch('/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert("Событие успешно создано!");
            window.location.href = "/";
        } else {
            const txt = await response.text();
            alert("Ошибка сервера: " + txt);
        }
    } catch (err) {
        console.error(err);
        alert("Ошибка соединения");
    }
}
