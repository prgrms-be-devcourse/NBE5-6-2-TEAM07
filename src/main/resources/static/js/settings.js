document.addEventListener("DOMContentLoaded", () => {
  const container = document.getElementById("ai-card-container");
  const selectedInput = document.createElement("input");
  selectedInput.type = "hidden";
  selectedInput.name = "selectedAiId";
  document.getElementById("aiForm").appendChild(selectedInput);

  let currentSelectedCard = null;

  fetch("/api/ai/list")
  .then(response => response.json())
  .then(data => {
    const aiList = data.aiInfoList;

    aiList.forEach((ai, index) => {
      const card = document.createElement("div");
      card.classList.add("ai-card");

      const button = document.createElement("button");
      button.classList.add("select-button");
      button.textContent = "선택";

      button.addEventListener("click", (e) => {
        e.preventDefault();

        // 이전 선택 해제
        if (currentSelectedCard) {
          currentSelectedCard.classList.remove("selected");
          const oldBtn = currentSelectedCard.querySelector(".select-button");
          oldBtn.textContent = "선택";
          oldBtn.classList.remove("selected");
          const oldIcon = oldBtn.querySelector("i");
          if (oldIcon) oldIcon.remove();
        }

        // 새로운 선택 표시
        card.classList.add("selected");
        button.classList.add("selected");
        button.textContent = "";
        const checkIcon = document.createElement("i");
        checkIcon.className = "fa-solid fa-check";
        button.appendChild(checkIcon);
        button.appendChild(document.createTextNode(" 선택됨"));

        // 선택 상태 저장
        currentSelectedCard = card;
        selectedInput.value = ai.id;
      });

      card.innerHTML = `
          <div class="ai-image"></div>
          <div class="ai-name">${ai.name}</div>
          <div class="ai-mbti">${ai.mbti}</div>
          <div class="ai-info">${ai.info}</div>
        `;
      card.appendChild(button);
      container.appendChild(card);
    });
  });
});
