const grid = document.querySelector(".calendar-grid");
const title = document.getElementById("calendar-title");
let currentDate = new Date();

function renderCalendar(date) {
  // 헤더 날짜 표시
  const year = date.getFullYear();
  const month = date.getMonth();
  title.textContent = `${year}.${String(month + 1).padStart(2, "0")}`;

  // 날짜 계산
  const firstDay = new Date(year, month, 1).getDay();
  const lastDate = new Date(year, month + 1, 0).getDate();

  // 기존 날짜 셀 제거
  document.querySelectorAll(".date-cell").forEach((el) => el.remove());

  // 빈칸 생성
  for (let i = 0; i < firstDay; i++) {
    const empty = document.createElement("div");
    empty.className = "date-cell";
    grid.appendChild(empty);
  }

  // 날짜 셀 생성
  const today = new Date();
  for (let d = 1; d <= lastDate; d++) {
    const cell = document.createElement("div");
    cell.className = "date-cell";

    const circle = document.createElement("div");
    circle.className = "date-circle";

    const number = document.createElement("div");
    number.className = "date-number";
    number.textContent = d;

    // 오늘 표시
    if (
        d === today.getDate() &&
        month === today.getMonth() &&
        year === today.getFullYear()
    ) {
      circle.classList.add("today-circle");
      number.classList.add("today-number");
    }

    cell.appendChild(circle);
    cell.appendChild(number);
    grid.appendChild(cell);
  }
}

document.getElementById("prev-month").addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() - 1);
  renderCalendar(currentDate);
});

document.getElementById("next-month").addEventListener("click", () => {
  currentDate.setMonth(currentDate.getMonth() + 1);
  renderCalendar(currentDate);
});

document.getElementById("go-today").addEventListener("click", () => {
  currentDate = new Date();
  renderCalendar(currentDate);
});

renderCalendar(currentDate);
