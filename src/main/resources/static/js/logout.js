
  function logout(event) {
  event.preventDefault();

  const form = document.createElement('form');
  form.method = 'post';
  form.action = '/member/logout';

  // CSRF 토큰 삽입
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const csrfParam = document.querySelector('meta[name="_csrf_parameter"]')?.getAttribute('content') || '_csrf';

  if (csrfToken) {
  const input = document.createElement('input');
  input.type = 'hidden';
  input.name = csrfParam;
  input.value = csrfToken;
  form.appendChild(input);
}

  document.body.appendChild(form);
  form.submit();
}
