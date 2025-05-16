const monthlyBtn = document.getElementById('monthly');
const yearlyBtn = document.getElementById('yearly');
const userId = 'user01'; // 로그인 세션에서 동적으로 처리 예정

monthlyBtn.addEventListener('click', () => {
  monthlyBtn.classList.add('selected');
  yearlyBtn.classList.remove('selected');
});

yearlyBtn.addEventListener('click', () => {
  yearlyBtn.classList.add('selected');
  monthlyBtn.classList.remove('selected');
});

// 유저 ID와 현재 기간(period)을 기준으로 API 호출
function fetchDiaryCount(userId, period, date = null) {
  let url = `/api/diary/dashboard/count?userId=${userId}&period=${period}`;
  if (date) {
    url += `&date=${date}`;
  }

  fetch(url)
  .then(res => res.json())
  .then(count => {
    document.querySelector('.diary-count-number').textContent = count;
  })
  .catch(err => {
    console.error("일기 수 조회 실패:", err);
  });
}

monthlyBtn.addEventListener('click', () => {
  monthlyBtn.classList.add('selected');
  yearlyBtn.classList.remove('selected');
  fetchDiaryCount(userId, 'monthly');
});

yearlyBtn.addEventListener('click', () => {
  yearlyBtn.classList.add('selected');
  monthlyBtn.classList.remove('selected');
  fetchDiaryCount(userId, 'yearly');
});

// 초기 로딩 시에도 한 번 호출
window.addEventListener('DOMContentLoaded', () => {
  fetchDiaryCount(userId, 'monthly');
});