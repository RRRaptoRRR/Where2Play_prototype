/**
 * create-event.js
 * Логика формы создания события: поиск, модальные окна, сохранение.
 */

// === ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ ===
// Хранят выбранные объекты (как существующие, так и новые)
let selectedGames = [];  // Формат: { id: 1, name: "...", isNew: false } или { name: "...", diff: "...", isNew: true }
let selectedThemes = [];
let selectedRules = [];  // Если решите добавить правила
let newPlace = null;     // Объект нового места, если создаем

// === ИНИЦИАЛИЗАЦИЯ ===
document.addEventListener('DOMContentLoaded', () => {

    // Кнопка "Назад"
    document.getElementById('btnBack').addEventListener('click', () => {
        if (confirm("Вы уверены, что хотите вернуться? Введенные данные не сохранятся.")) {
            window.location.href = "/";
        }
    });

    // Обработка отправки формы
    document.getElementById('createEventForm').addEventListener('submit', handleFormSubmit);
});

// === ФУНКЦИИ МОДАЛЬНЫХ ОКОН ===
function openModal(id) {
    document.getElementById(id).style.display = 'flex';
    // Очистка полей при открытии (опционально)
}

function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

// === ЛОГИКА МЕСТА (PLACE) ===

// 1. Сохранение НОВОГО места из модалки
function saveNewPlace() {
    const name = document.getElementById('newPlaceName').value.trim();
    if (!name) { alert("Введите название места"); return; }

    newPlace = {
        name: name,
        city: document.getElementById('newPlaceCity').value,
        district: document.getElementById('newPlaceDistrict').value,
        address: document.getElementById('newPlaceAddress').value,
        description: "", // Можно добавить поле в модалку
        additionalInfo: ""
    };

    // Отображаем интерфейс "Выбрано"
    document.getElementById('selectedPlaceName').innerText = newPlace.name + " (Новое)";
    document.getElementById('selectedPlace').style.display = 'flex';
    document.getElementById('placeSearch').style.display = 'none';
    document.getElementById('placeId').value = ""; // ID пустой, т.к. новое

    closeModal('placeModal');
}

// 2. Удаление выбранного места (сброс)
function removePlace() {
    newPlace = null;
    document.getElementById('placeId').value = "";
    document.getElementById('selectedPlace').style.display = 'none';
    const searchInput = document.getElementById('placeSearch');
    searchInput.style.display = 'block';
    searchInput.value = "";
}

// 3. Выбор СУЩЕСТВУЮЩЕГО места из поиска
function selectPlace(place) {
    document.getElementById('placeId').value = place.id;
    document.getElementById('selectedPlaceName').innerText = place.name;
    document.getElementById('selectedPlace').style.display = 'flex';
    document.getElementById('placeSearch').style.display = 'none';
    document.getElementById('placeResults').innerHTML = ""; // Очистить результаты
    newPlace = null; // Сбрасываем объект нового места
}

// 4. Поиск мест (AJAX)
async function searchPlaces(query) {
    const resultsDiv = document.getElementById('placeResults');
    resultsDiv.innerHTML = "";

    if (query.trim().length < 2) return;

    try {
        const response = await fetch(`/api/search/places?query=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Ошибка сети');

        const places = await response.json();

        if (places.length === 0) {
            resultsDiv.innerHTML = "<div class='search-item' style='color:#999; cursor:default'>Ничего не найдено</div>";
            return;
        }

        places.forEach(p => {
            const div = document.createElement('div');
            div.className = 'search-item';
            div.innerText = p.name;
            div.onclick = () => selectPlace(p);
            resultsDiv.appendChild(div);
        });
    } catch (e) {
        console.error("Ошибка поиска мест:", e);
    }
}

// === ЛОГИКА ИГР (GAMES) ===

// 1. Сохранение НОВОЙ игры
function saveNewGame() {
    const name = document.getElementById('newGameName').value.trim();
    if (!name) { alert("Введите название игры"); return; }

    const game = {
        name: name,
        difficulty: document.getElementById('newGameDiff').value,
        description: "", // Можно расширить форму
        isNew: true
    };

    addGameToSelection(game);
    // Очистка полей
    document.getElementById('newGameName').value = "";
    closeModal('gameModal');
}

// 2. Добавление игры в список выбранных
function addGameToSelection(game) {
    // Проверка на дубликаты
    const exists = selectedGames.some(g => g.name.toLowerCase() === game.name.toLowerCase());
    if (exists) { alert("Эта игра уже добавлена"); return; }

    selectedGames.push(game);
    renderGameTags();

    // Очистить поиск
    const searchInput = document.querySelector('input[oninput="searchGames(this.value)"]');
    if(searchInput) {
        searchInput.value = "";
        document.getElementById('gameResults').innerHTML = "";
    }
}

// 3. Выбор существующей игры
function selectGame(game) {
    // Преобразуем формат из API поиска в формат внутреннего списка
    const gameObj = {
        id: game.id,
        name: game.name,
        isNew: false
    };
    addGameToSelection(gameObj);
}

// 4. Рендер тегов игр
function renderGameTags() {
    const container = document.getElementById('selectedGames');
    container.innerHTML = "";

    selectedGames.forEach((g, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        // Если новая - добавим пометку (*)
        const label = g.isNew ? `${g.name} (New)` : g.name;
        tag.innerHTML = `${label} <span onclick="removeGame(${index})">&times;</span>`;
        container.appendChild(tag);
    });
}

// 5. Удаление игры из списка
function removeGame(index) {
    selectedGames.splice(index, 1);
    renderGameTags();
}

// 6. Поиск игр (AJAX)
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
    } catch (e) {
        console.error(e);
    }
}

// === ЛОГИКА ТЕМ (THEMES) - Аналогично играм ===

function saveNewTheme() {
    const name = document.getElementById('newThemeName').value.trim();
    if (!name) return;

    const theme = { name: name, isNew: true };
    addThemeToSelection(theme);
    document.getElementById('newThemeName').value = "";
    closeModal('themeModal');
}

function addThemeToSelection(theme) {
    if (selectedThemes.some(t => t.name.toLowerCase() === theme.name.toLowerCase())) return;
    selectedThemes.push(theme);
    renderThemeTags();

    const searchInput = document.querySelector('input[oninput="searchThemes(this.value)"]');
    if(searchInput) {
        searchInput.value = "";
        document.getElementById('themeResults').innerHTML = "";
    }
}

function selectTheme(theme) {
    addThemeToSelection({ id: theme.id, name: theme.name, isNew: false });
}

function renderThemeTags() {
    const container = document.getElementById('selectedThemes');
    container.innerHTML = "";
    selectedThemes.forEach((t, index) => {
        const tag = document.createElement('div');
        tag.className = 'tag';
        const label = t.isNew ? `${t.name} (*)` : t.name;
        tag.innerHTML = `${label} <span onclick="removeTheme(${index})">&times;</span>`;
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


// === ОТПРАВКА ФОРМЫ НА СЕРВЕР ===
async function handleFormSubmit(e) {
    e.preventDefault();

    // Валидация места
    const placeId = document.getElementById('placeId').value;
    if (!placeId && !newPlace) {
        alert("Пожалуйста, выберите или создайте место проведения.");
        return;
    }

    const formData = new FormData(e.target);

    // Собираем DTO объект
    const dto = {
        name: formData.get('name'),
        description: formData.get('description'),
        date: formData.get('date'),
        maxPlayers: formData.get('maxPlayers'),

        // Место
        placeId: placeId ? parseInt(placeId) : null,
        newPlace: newPlace, // null или объект

        // Игры: разделяем на ID существующих и объекты новых
        gameIds: selectedGames.filter(g => !g.isNew).map(g => g.id),
        newGames: selectedGames.filter(g => g.isNew).map(g => ({
            name: g.name,
            difficulty: g.difficulty,
            description: g.description
        })),

        // Темы
        themeIds: selectedThemes.filter(t => !t.isNew).map(t => t.id),
        newThemes: selectedThemes.filter(t => t.isNew).map(t => ({ name: t.name }))
    };

    console.log("Отправка DTO:", dto);

    try {
        const response = await fetch('/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert("Событие успешно создано!");
            window.location.href = "/"; // Возврат в меню
        } else {
            const errorText = await response.text();
            alert("Ошибка при создании: " + errorText);
        }
    } catch (error) {
        console.error("Ошибка сети:", error);
        alert("Произошла ошибка сети при отправке данных.");
    }
}

// Делаем функции глобально доступными (чтобы работали onclick в HTML)
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
