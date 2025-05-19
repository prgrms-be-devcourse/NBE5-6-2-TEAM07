async function loadRecentDiaries(userId) {
  const response = await fetch(`/api/diary/cards?userId=${userId}`);
  const data = await response.json();
  const diaries = data.diaryCards;

  const container = document.getElementById("diary-card-container");
  const template = document.getElementById("diary-card-template");

  diaries.slice(0, 14).forEach((diary) => {
    const clone = template.content.cloneNode(true);
    const card = clone.querySelector(".diary-card");

    // 배경/글자 설정
    if (diary.imagePath) {
      card.classList.add("with-image");
      card.style.backgroundImage = `url('${diary.imagePath}')`;
    } else {
      card.classList.add("no-image");
    }

    // 이모지
    const icon = clone.querySelector(".emotion-icon");
    const fileName = emotionImageMap[diary.emotion?.toUpperCase()] || emotionImageMap.DEFAULT;
    icon.src = `/images/emotion/weather/${fileName}`;

    // 날짜 (ex. 2025-05-08 → 05.08)
    const dateElem = clone.querySelector(".diary-date");
    const [year, month, day] = diary.createdAt.split("-");
    dateElem.textContent = `${month}.${day}`;

    // 일기 내용
    const right = clone.querySelector(".diary-right");
    right.textContent = diary.content || "(내용 없음)";

    // 상세 페이지로 이동 이벤트 추가
    card.addEventListener("click", () => {
      const dateParam = diary.date;
      window.location.href = `/diary/record?date=${dateParam}`;
    });

    container.appendChild(clone);
  });
}
document.addEventListener("DOMContentLoaded", () => {
  //TODO : Auth 구현이 완료되면 사용자 아이디 동적으로 받아올 수 있도록 할 것
  const userId = "user01"; // 실제 사용자 ID로 교체
  loadRecentDiaries(userId);
});
