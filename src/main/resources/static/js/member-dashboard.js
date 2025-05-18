const monthlyBtn = document.getElementById('monthly');
const yearlyBtn = document.getElementById('yearly');
const userId = 'user01';

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

function fetchKeywordRank(userId, period, date = null) {
  let url = `/api/keyword/ranking?userId=${userId}&period=${period}`;
  if (date) {
    url += `&date=${date}`;
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

function formatDate(date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0');
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function getMonthRange(date) {
  const year = date.getFullYear();
  const month = date.getMonth(); // 0-based
  const start = new Date(year, month, 1);
  const end = new Date(year, month + 1, 0);
  return {
    unit: 'day',
    displayFormat: 'M/d',
    min: formatDate(start), // ✅ 로컬 날짜 사용
    max: formatDate(end)
  };
}

function getYearRange(date) {
  const year = date.getFullYear();
  return {
    unit: 'month',
    displayFormat: 'M월',
    min: `${year}-01-01`,
    max: `${year}-12-31`
  };
}

async function drawEmotionChart(userId, period = 'monthly', date = null) {
  const emotionImageMap = window.emotionImageMap;
  const ctx = document.getElementById('emotionChart').getContext('2d');
  const emotionOrder = ['VERY_GOOD', 'GOOD', 'NORMAL', 'BAD', 'VERY_BAD'];
  const orderedEmotions = emotionOrder.filter(e => emotionImageMap[e]);

  const moodValueMap = {};
  orderedEmotions.forEach((key, idx) => {
    moodValueMap[key] = orderedEmotions.length - idx;
  });

  const baseDate = date ? new Date(date) : new Date();
  const range = period === 'monthly' ? getMonthRange(baseDate) : getYearRange(baseDate);

  let url = `/api/diary/emotion/flow?userId=${userId}&period=${period}`;
  if (date) {
    url += `&date=${date}`;
  }

  const response = await fetch(url);
  const json = await response.json();
  const diaryList = json.diaryMonthlyEmotionList;

  const moodData = diaryList.map(item => ({
    x: item.date,
    y: moodValueMap[item.emotion]
  }));

  const chart = new Chart(ctx, {
    type: 'line',
    data: {
      datasets: [{
        data: moodData,
        borderColor: '#467750',
        tension: 0,
        pointRadius: 3,
        pointBackgroundColor: '#467750',
      }]
    },
    options: {
      layout: { padding: { right: 60 } },
      responsive: true,
      maintainAspectRatio: false,
      devicePixelRatio: window.devicePixelRatio || 1,
      plugins: {
        legend: { display: false },
        tooltip: { enabled: false }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: range.unit,
            displayFormats: { day: 'M/d', month: 'M월' }
          },
          min: range.min,
          max: range.max,
          ticks: {
            color: '#333',
            maxTicksLimit: period === 'monthly' ? 7 : 12,
            font: { size: 12 }
          },
          grid: { color: '#eee', drawBorder: false }
        },
        y: {
          min: 0.5,
          max: 5.5,
          ticks: {
            stepSize: 1,
            display: false
          },
          grid: {
            display: false,
            drawBorder: false
          }
        }
      }
    }
  });

  setTimeout(() => {
    const yAxis = chart.scales.y;
    const yAxisContainer = document.getElementById('yAxisLabels');
    if (!yAxis || !yAxisContainer) return;

    yAxisContainer.innerHTML = '';
    orderedEmotions.forEach((emotionKey) => {
      const yValue = moodValueMap[emotionKey];
      const yPixel = yAxis.getPixelForValue(yValue);
      const img = document.createElement('img');
      img.src = `/images/emotion/weather/${emotionImageMap[emotionKey]}`;
      img.alt = emotionKey;
      img.style.position = 'absolute';
      img.style.left = '0';
      img.style.top = `${yPixel}px`;
      img.style.width = '40px';
      img.style.height = '40px';
      img.style.transform = 'translateY(-50%)';
      yAxisContainer.appendChild(img);
    });
  }, 50);
}

function handlePeriodChange(period) {
  const today = new Date();
  const todayStr = today.toLocaleDateString('sv-SE'); // ✅ '2025-05-17' 형식 유지
  fetchDiaryCount(userId, period, todayStr);
  fetchKeywordRank(userId, period, todayStr);
  drawEmotionChart(userId, period, todayStr);
}

window.addEventListener('DOMContentLoaded', () => {
  handlePeriodChange('monthly');
});
