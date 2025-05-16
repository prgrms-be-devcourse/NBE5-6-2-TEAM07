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

// 유저 ID와 날짜를 기준으로 연간/월간 키워드 랭킹 API 호출
function fetchKeywordRank(userId, period, date=null){
  let url = `/api/keyword/ranking?userId=${userId}&period=${period}`;
  if(date){
    url += `date=${date}`;
  }

  fetch(url)
  .then(res => res.json())
  .then(data => {
    const ranks = data.keywordRankList;
    const cardEls = document.querySelectorAll('.keyword-card');

    for (let i = 0; i < cardEls.length; i++) {
      const card = cardEls[i];
      const rankEl = card.querySelector('.rank');
      const wordEl = card.querySelector('.word');
      const countEl = card.querySelector('.count');

      if (i < ranks.length) {
        rankEl.textContent = `${i + 1}`;
        wordEl.textContent = ranks[i].name;
        countEl.textContent = `${ranks[i].count}회`;
      } else {
        // 데이터 부족 시 placeholder 처리
        rankEl.textContent = `${i + 1}`;
        wordEl.textContent = "-";
        countEl.textContent = "-회";
      }
    }
  })
  .catch(err => {
    console.error("키워드 랭킹 조회 실패:", err);
  });
}

function handlePeriodChange(period) {
  fetchDiaryCount(userId, period);
  fetchKeywordRank(userId, period);
}

monthlyBtn.addEventListener('click', () => {
  monthlyBtn.classList.add('selected');
  yearlyBtn.classList.remove('selected');
  handlePeriodChange('monthly');
});

yearlyBtn.addEventListener('click', () => {
  yearlyBtn.classList.add('selected');
  monthlyBtn.classList.remove('selected');
  handlePeriodChange('yearly');
});

// 초기 로딩 시에도 한 번 호출
window.addEventListener('DOMContentLoaded', () => {
  handlePeriodChange('monthly');
});