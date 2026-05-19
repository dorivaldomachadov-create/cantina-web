document.addEventListener('DOMContentLoaded', () => {
  const tema = localStorage.getItem('tema');
  if (tema === 'dark') {
    document.documentElement.setAttribute('data-theme', 'dark');
    const icon = document.getElementById('theme-icon');
    if (icon) icon.className = 'fas fa-sun';
  }
});

function toggleTheme() {
  const html = document.documentElement;
  const icon = document.getElementById('theme-icon');
  if (html.getAttribute('data-theme') === 'dark') {
    html.removeAttribute('data-theme');
    if (icon) icon.className = 'fas fa-moon';
    localStorage.setItem('tema', 'light');
  } else {
    html.setAttribute('data-theme', 'dark');
    if (icon) icon.className = 'fas fa-sun';
    localStorage.setItem('tema', 'dark');
  }
}

function toggleSidebar() {
  document.querySelector('.sidebar')?.classList.toggle('open');
}

function confirmarCancelar(url) {
  if (confirm('Tens a certeza que queres cancelar esta venda? O stock será devolvido.')) {
    window.location.href = url;
  }
}

function confirmarRemover(form) {
  if (confirm('Remover este item da venda?')) form.submit();
  return false;
}

function autoSubmitBusca() {
  const idInput   = document.getElementById('idProduto');
  const nomeInput = document.getElementById('nomeProduto');
  const form      = document.getElementById('form-busca');
  if (!form) return;

  [idInput, nomeInput].forEach(el => {
    if (!el) return;
    el.addEventListener('keydown', e => {
      if (e.key === 'Enter') { e.preventDefault(); form.submit(); }
    });
  });
}

autoSubmitBusca();

function printFatura() {
  window.print();
}

document.querySelectorAll('.qty-input').forEach(input => {
  input.addEventListener('change', () => {
    const val = parseInt(input.value);
    const max = parseInt(input.getAttribute('max') || 9999);
    if (isNaN(val) || val < 1) input.value = 1;
    if (val > max) input.value = max;
  });
});
