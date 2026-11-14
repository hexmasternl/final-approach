document.addEventListener('click', function (e) {
  var b = e.target.closest('[data-theme-set]'); if (!b) return;
  document.documentElement.setAttribute('data-theme', b.getAttribute('data-theme-set'));
  document.documentElement.style.colorScheme = b.getAttribute('data-theme-set') === 'dark' ? 'dark' : 'light';
  document.querySelectorAll('[data-theme-set]').forEach(function (x) { x.setAttribute('aria-pressed', x === b ? 'true' : 'false'); });
});

  document.addEventListener('click', function (e) {
    var t = e.target.closest('.fa-toggle');
    if (t && !t.disabled) t.setAttribute('aria-checked', t.getAttribute('aria-checked') === 'true' ? 'false' : 'true');
  });


  document.addEventListener('click', function (e) {
    var s = e.target.closest('.fa-segment'); if (!s) return;
    s.parentNode.querySelectorAll('.fa-segment').forEach(function (b) { b.setAttribute('aria-pressed', b === s ? 'true' : 'false'); });
    document.getElementById('dn').textContent = s.getAttribute('data-note');
  });

