(() => {
  const styleSel = document.getElementById('styleSelector')
  if (!styleSel) return
  const themes = [
    { file: '', label: 'Default(无)' },
    { file: 'win98.css', label: 'Win98/XP' },
    { file: 'ios.css', label: 'iOS毛玻璃' },
    { file: 'linux.css', label: 'Linux风格' }
  ]
  styleSel.innerHTML = ''
  themes.forEach(th => {
    const opt = document.createElement('option')
    opt.value = th.file
    opt.textContent = th.label
    styleSel.appendChild(opt)
  })
  styleSel.value = ''
})()
