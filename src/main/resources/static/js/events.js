document.addEventListener('DOMContentLoaded', function() {
    // Инициализация ввода тегов при загрузке страницы
    const gameInput = document.getElementById('gameInput');

    if(gameInput) {
        gameInput.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && this.value.trim() !== '') {
                addTag(this.value.trim());
                this.value = '';
                filterTable();
            }
        });
    }
});

/* --- Логика тегов --- */
let gameTags = [];

function addTag(text) {
    if (gameTags.includes(text.toLowerCase())) return;

    gameTags.push(text.toLowerCase());

    const tagsContainer = document.querySelector('.tags-input-container');
    const gameInput = document.getElementById('gameInput');

    const tag = document.createElement('div');
    tag.className = 'tag';
    tag.innerHTML = `${text} <i class="fa-solid fa-xmark" onclick="removeTag('${text}', this)"></i>`;

    tagsContainer.insertBefore(tag, gameInput);
}

function removeTag(text, element) {
    gameTags = gameTags.filter(t => t !== text.toLowerCase());
    element.parentElement.remove();
    filterTable();
}

/* --- Логика Фильтрации --- */
function filterTable() {
    const organizer = document.getElementById('filterOrganizer').value.toLowerCase();
    const date = document.getElementById('filterDate').value; // yyyy-mm-dd
    const city = document.getElementById('filterCity').value.toLowerCase();
    const district = document.getElementById('filterDistrict').value.toLowerCase();

    const rows = document.querySelectorAll('#eventsTable tbody tr');

    rows.forEach(row => {
        // Индексы ячеек должны совпадать с events.html
        const rowOrganizer = row.cells[1].textContent.toLowerCase();

        // Парсинг даты (dd.MM.yyyy HH:mm) -> ISO yyyy-mm-dd
        const rowDateRaw = row.cells[2].textContent.trim();
        const [d, m, y] = rowDateRaw.split(' ')[0].split('.');
        const rowDateIso = `${y}-${m}-${d}`;

        const rowCity = row.cells[6].textContent.toLowerCase();
        const rowDistrict = row.cells[7].textContent.toLowerCase();
        const rowGames = row.cells[9].textContent.toLowerCase();

        let show = true;

        if (organizer && !rowOrganizer.includes(organizer)) show = false;
        if (date && rowDateIso !== date) show = false;
        if (city && !rowCity.includes(city)) show = false;
        if (district && !rowDistrict.includes(district)) show = false;

        // Фильтр по тегам (И)
        if (gameTags.length > 0) {
            const hasAllTags = gameTags.every(tag => rowGames.includes(tag));
            if (!hasAllTags) show = false;
        }

        row.style.display = show ? '' : 'none';
    });
}

/* --- Логика Сортировки --- */
function sortTable(n) {
    const table = document.getElementById("eventsTable");
    let switching = true, shouldSwitch, dir = "asc", switchcount = 0;

    while (switching) {
        switching = false;
        let rows = table.rows;

        for (let i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            let x = rows[i].getElementsByTagName("TD")[n];
            let y = rows[i + 1].getElementsByTagName("TD")[n];

            let xVal = x.textContent.toLowerCase();
            let yVal = y.textContent.toLowerCase();

            // Если сортируем по дате (колонка 2), можно добавить улучшенное сравнение
            // но для базовой версии лексикографическое сравнение yyyy подходит,
            // а для dd.mm.yyyy лучше перевернуть строку, но оставим пока текст.

            if (dir == "asc") {
                if (xVal > yVal) { shouldSwitch = true; break; }
            } else if (dir == "desc") {
                if (xVal < yVal) { shouldSwitch = true; break; }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            switchcount++;
        } else {
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}

/* --- AJAX Запросы --- */
async function joinEvent(eventId, btn) {
    try {
        const response = await fetch(`/events/${eventId}/join`, {
            method: 'POST'
        });

        if (response.ok) {
            btn.textContent = "Отписаться";
            btn.classList.add("btn-leave");
            btn.setAttribute("onclick", `leaveEvent(${eventId}, this)`);
            updateCounter(btn, 1);
        } else {
            const text = await response.text();
            alert("Ошибка: " + text);
        }
    } catch (e) {
        console.error(e);
        alert("Ошибка сети");
    }
}

function leaveEvent(eventId, btn) {
    if (!confirm("Вы уверены, что хотите отписаться?")) return;

    fetch(`/events/${eventId}/leave`, {
        method: 'POST'
    }).then(response => {
        if (response.ok) {
            btn.textContent = "Записаться";
            btn.classList.remove("btn-leave");
            btn.setAttribute("onclick", `joinEvent(${eventId}, this)`);
            updateCounter(btn, -1);
        } else {
            alert("Ошибка при отписке");
        }
    });
}

function updateCounter(btn, change) {
    const row = btn.closest('tr');
    const counterSpan = row.querySelector('.current-players');
    let current = parseInt(counterSpan.textContent);
    counterSpan.textContent = current + change;
}
