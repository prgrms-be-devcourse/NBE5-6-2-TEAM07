const monthlyBtn = document.getElementById('monthly');
const yearlyBtn = document.getElementById('yearly');

monthlyBtn.addEventListener('click', () => {
  monthlyBtn.classList.add('selected');
  yearlyBtn.classList.remove('selected');
});

yearlyBtn.addEventListener('click', () => {
  yearlyBtn.classList.add('selected');
  monthlyBtn.classList.remove('selected');
});