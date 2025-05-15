const form = document.querySelector('form');
const contentTextarea = document.querySelector('textarea[name="content"]');
const charCountSpan = document.getElementById('char-count');
const emotionInputs = document.querySelectorAll('input[type="radio"][name$="emotion"]');
const primaryContainer = document.getElementById('keyword-primary');
const secondaryContainer = document.getElementById('keyword-secondary');
const toggleBtn = document.querySelector('.toggle-btn');

let currentType = 'good';
let expanded = false;

console.log('emotionInputs:', emotionInputs);
emotionInputs.forEach(input => {
  console.log(input.name, input.checked);
});

form.addEventListener('submit', function (e) {
  const emotionChecked = document.querySelector('input[type="radio"][name="emotion"]:checked');
  console.log('remon', emotionChecked);

  if (!emotionChecked) {
    e.preventDefault();
    alert("오늘의 감정을 선택해주세요.");
    document.getElementById('emotion1').scrollIntoView({ behavior: 'smooth' });
    return;
  }


  if (contentTextarea.value.length > 1000) {
    e.preventDefault();
    alert("일기는 1000자 이내로 작성해주세요.");
  }
});

// DOM 로드 후 기본 감정 키워드 표시
window.addEventListener('DOMContentLoaded', () => {
  renderPrimary('good');
  toggleBtn.innerText = '+ 더보기';
  updateCharCount(); // 초기 글자수 세팅
});

// 감정 선택 시 키워드 그룹 전환
emotionInputs.forEach(input => {
  input.addEventListener('change', function () {
    const value = this.value;
    if (['VERY_GOOD', 'GOOD', 'COMMON'].includes(value)) {
      currentType = 'good';
      renderPrimary('good');
    } else {
      currentType = 'bad';
      renderPrimary('bad');
    }
    hideSecondary();
    toggleBtn.innerText = '+ 더보기';
    expanded = false;
  });
});

// 키워드 렌더링 함수
function renderPrimary(type) {
  primaryContainer.innerHTML = '';
  const items = document.querySelectorAll(`#keyword-${type} .keyword-chip`);
  items.forEach(el => primaryContainer.appendChild(el.cloneNode(true)));
}

function renderSecondary(type) {
  secondaryContainer.innerHTML = '';
  const items = document.querySelectorAll(`#keyword-${type} .keyword-chip`);
  items.forEach(el => secondaryContainer.appendChild(el.cloneNode(true)));
  secondaryContainer.style.display = 'flex';
}

function hideSecondary() {
  secondaryContainer.innerHTML = '';
  secondaryContainer.style.display = 'none';
}

function toggleKeywordExpand(btn) {
  if (expanded) {
    hideSecondary();
    btn.innerText = '+ 더보기';
  } else {
    const opposite = currentType === 'good' ? 'bad' : 'good';
    renderSecondary(opposite);
    btn.innerText = '- 접기';
  }
  expanded = !expanded;
}

// 이미지 미리보기
function previewImages(event) {
  const files = event.target.files;
  const previewArea = document.getElementById('preview-area');

  // 사진기 아이콘 placeholder 아이콘 제거
  const placeholder = previewArea.querySelector('.placeholder-icon');
  if (placeholder) previewArea.removeChild(placeholder);

  Array.from(files).forEach(file => {
    const reader = new FileReader();
    reader.onload = function (e) {
      const img = document.createElement('img');
      img.src = e.target.result;
      img.classList.add('preview-img');
      previewArea.appendChild(img);
    };
    reader.readAsDataURL(file);
  });
}

// 일기 글자 수 체크 + 제한
contentTextarea.addEventListener('input', updateCharCount);

function updateCharCount() {
  const length = contentTextarea.value.length;
  charCountSpan.textContent = `${length} / 1000자`;

  if (length > 1000) {
    charCountSpan.style.color = 'red';
  } else {
    charCountSpan.style.color = '#555';
  }
}