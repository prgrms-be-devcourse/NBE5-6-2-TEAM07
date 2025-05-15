let currentPage = 0;
let isLoading = false;
const PAGE_SIZE = 30;

document.addEventListener("DOMContentLoaded", () => {
  const userId = "user01"; // TODO: 실제 로그인된 사용자 ID로 대체
  loadNextPage(userId);

  // 무한 스크롤 감지
  window.addEventListener("scroll", () => {
    if (
        window.innerHeight + window.scrollY >= document.body.offsetHeight - 100 &&
        !isLoading
    ) {
      loadNextPage(userId);
    }
  });
});

async function loadNextPage(userId) {
  isLoading = true;

  try {
    const response = await fetch(
        `/api/diary/cards?userId=${userId}&page=${currentPage}`
    );
    const data = await response.json();
    const diaries = data.diaryCards || [];

    if (diaries.length > 0) {
      const container = document.getElementById("diary-card-container");
      diaries.forEach((diary) => {
        const card = renderDiaryCard(diary);
        container.appendChild(card);
      });
      currentPage++;
    } else {
      // 더 이상 불러올 일기가 없음
      window.removeEventListener("scroll", onScroll);
    }
  } catch (error) {
    console.error("일기 불러오기 실패:", error);
  } finally {
    isLoading = false;
  }
}

function renderDiaryCard(diary) {
  const template = document.getElementById("diary-card-timeline-template");
  const clone = template.content.cloneNode(true);
  const card = clone.querySelector(".diary-card");

  // 날짜 포맷
  const [year, month, day] = diary.createdAt.split("-");
  const dateElem = clone.querySelector(".diary-date");
  dateElem.textContent = `${month}.${day}`;

  // 감정 이미지
  const emotionKey = diary.emotion?.toUpperCase() || "DEFAULT";
  const icon = clone.querySelector(".emotion-icon");
  icon.src = `/images/emotion/weather/${emotionImageMap[emotionKey]}`;

  // 일기 내용
  const contentElem = clone.querySelector(".diary-content");
  contentElem.textContent = diary.content || "(내용 없음)";

  // 이미지 처리
  if (!diary.imagePath) {
    card.classList.add("no-image");
  } else {
    const img = clone.querySelector(".diary-image");
    img.src = diary.imagePath;
  }

  return clone;
}
