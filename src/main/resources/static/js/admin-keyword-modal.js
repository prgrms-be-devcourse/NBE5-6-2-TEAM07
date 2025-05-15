document.addEventListener('DOMContentLoaded', () => {
  const modal = document.getElementById('keywordWriteModal');
  const openModalBtn = document.getElementById('add-keyword');
  const closeModalBtn = document.getElementById('modal-close');
  const submitBtn = document.getElementById('modal-submit');
  const segmentButtons = document.querySelectorAll('.segment-button');
  const typeSelect = document.getElementById('keyword-input-type');
  const specificTypeDiv = document.querySelector('.keyword-input-specific-type');

  const typeInitial = {
    'EMOTION_GOOD': 'EMOTION',
    'EMOTION_BAD': 'EMOTION',
    'PERSON': 'PERSON',
    'SITUATION': 'SITUATION',
  };

  function initializeModalType() {
    typeSelect.value = segmentButtons.value;
  }

  initializeModalType();

  function toggleSpecificType() {
    if (typeSelect.value === 'EMOTION') {
      specificTypeDiv.style.display = 'block';
    } else {
      specificTypeDiv.style.display = 'none';
    }
  }

  toggleSpecificType();

  typeSelect.addEventListener('change', () => {
    toggleSpecificType();
  });

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

  function setActiveKeywordType(type) {
    const buttons = document.querySelectorAll('.segment-button');
    buttons.forEach(button => {
      if (button.dataset.type === type) {
        button.classList.add('active');
      } else {
        button.classList.remove('active');
      }
    });
  }

  // 모달 제출
  submitBtn.addEventListener('click', () => {
    const nameInput = document.getElementById('keyword-input-name');
    const typeSelect = document.getElementById('keyword-input-type');
    const name = nameInput.value.trim();
    let keywordType = typeSelect.value;

    if (keywordType === 'EMOTION') {
      const specificTypeRadios = document.querySelectorAll('input[name="specific-type"]');
      let specificTypeValue = null;
      specificTypeRadios.forEach(radio => {
        if (radio.checked) specificTypeValue = radio.value;
      });

      if (specificTypeValue === 'GOOD') {
        keywordType = 'EMOTION_GOOD';
      } else if (specificTypeValue === 'BAD') {
        keywordType = 'EMOTION_BAD';
      }
    }

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
      const mainType = typeInitial[keywordType];
      fetchKeywords(mainType);
      setActiveKeywordType(mainType);
    })
    .catch(err => {
      alert('등록 중 오류가 발생했습니다.');
      console.error(err);
    });
  });

  function clearModalFields() {
    document.getElementById('keyword-input-name').value = '';

    const typeSelect = document.getElementById('keyword-input-type');
    typeSelect.value = 'EMOTION'; // 기본값으로 리셋

    // 세부 타입 라디오 버튼 초기화
    const firstRadio = document.querySelector('input[name="specific-type"][value="GOOD"]');
    if (firstRadio) firstRadio.checked = true;

    toggleSpecificType();
  }

});
