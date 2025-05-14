document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('keywordWriteModal');
  const openModalBtn = document.getElementById('add-keyword');
  const closeModalBtn = document.getElementById('modal-close');
  const submitBtn = document.getElementById('modal-submit');

  // 모달 열기
  openModalBtn.addEventListener('click', () => {
    modal.style.display = 'block';
  });

  // 모달 닫기
  closeModalBtn.addEventListener('click', () => {
    modal.style.display = 'none';
    clearModalFields();
  });

  // 바깥 클릭 시 닫기
  window.addEventListener('click', (event) => {
    if (event.target === modal) {
      modal.style.display = 'none';
      clearModalFields();
    }
  });

  // 모달 제출
  submitBtn.addEventListener('click', () => {
    const nameInput = document.getElementById('modal-keyword-name');
    const typeSelect = document.getElementById('modal-keyword-type');

    const name = nameInput.value.trim();
    const keywordType = typeSelect.value;

    if (!name) {
      alert('키워드 이름을 입력하세요.');
      return;
    }

    const requestBody = {
      id: null,
      name,
      keywordType
    };

    fetch('/api/admin/keyword', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    })
    .then(res => {
      if (!res.ok) throw new Error('등록 실패');
      return res.json();
    })
    .then(() => {
      modal.style.display = 'none';
      clearModalFields();
      fetchKeywords(keywordType);
    })
    .catch(err => {
      alert('등록 중 오류가 발생했습니다.');
      console.error(err);
    });
  });

  // 입력 초기화
  function clearModalFields() {
    document.getElementById('modal-keyword-name').value = '';
    document.getElementById('modal-keyword-type').selectedIndex = 0;
  }

  // keywordType 문자 변환
  function getKeywordTypeParam(label) {
    switch (label) {
      case '감정': return 'EMOTION';
      case '사람': return 'PERSON';
      case '상황': return 'SITUATION';
      default: return 'EMOTION';
    }
  }
});
