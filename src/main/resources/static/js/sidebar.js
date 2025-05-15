const currentPath = window.location.pathname;

document.querySelectorAll(".sidebar .menu a").forEach(link => {
  const linkPath = link.getAttribute("href");
  if (currentPath === linkPath) {
    link.classList.add("active");
  }
});
