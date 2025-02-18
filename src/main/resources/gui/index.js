let ws = null;
let modulesData = [];
let currentCategory = null;
let messageTimer = null;

let soundEnabled = false;
let enableSoundUrl = '';
let disableSoundUrl = '';
let audioEnable = null;
let audioDisable = null;

let iosBlurEnabled = true;
let iosCardBlurDisabled = false;
let win98AnimEnabled = true;
let linuxBlinkEnabled = true;

let bindPopupModule = null;

document.addEventListener('DOMContentLoaded', () => {
  loadSettings();
  connectWebSocket();

  document.getElementById('searchInput').addEventListener('input', () => {
    renderModules();
    saveSettings();
  });

  document.getElementById('themeToggleBtn').addEventListener('click', () => {
    const isDark = document.body.classList.contains('dark-mode');
    setDarkMode(!isDark);
    document.getElementById('darkModeCheckbox').checked = !isDark;
  });

  document.getElementById('openSettingsBtn').addEventListener('click', () => openSettings(true));
  document.getElementById('closeSettingsBtn').addEventListener('click', () => openSettings(false));

  document.getElementById('darkModeCheckbox').addEventListener('change', e => {
    setDarkMode(e.target.checked);
  });

  document.getElementById('soundEnableCheckbox').addEventListener('change', e => {
    soundEnabled = e.target.checked;
    saveSettings();
  });

  document.getElementById('soundUrlEnable').addEventListener('change', e => {
    enableSoundUrl = e.target.value.trim();
    audioEnable = enableSoundUrl ? new Audio(enableSoundUrl) : null;
    saveSettings();
  });

  document.getElementById('soundUrlDisable').addEventListener('change', e => {
    disableSoundUrl = e.target.value.trim();
    audioDisable = disableSoundUrl ? new Audio(disableSoundUrl) : null;
    saveSettings();
  });

  document.getElementById('bgUrlInput').addEventListener('change', e => {
    setCustomBackground(e.target.value);
    saveSettings();
  });

  document.getElementById('layoutSelector').addEventListener('change', e => {
    applyLayout(e.target.value);
    animateModulesEnter();
    renderModules();
    saveSettings();
  });

  initStyleSelector();
  initBindPopup();

  document.getElementById('iosBlurCheckbox').addEventListener('change', e => {
    iosBlurEnabled = e.target.checked;
    applyThemeSettings();
    saveSettings();
  });

  document.getElementById('win98AnimCheckbox').addEventListener('change', e => {
    win98AnimEnabled = e.target.checked;
    applyThemeSettings();
    saveSettings();
  });

  document.getElementById('linuxTerminalBlinkCheckbox').addEventListener('change', e => {
    linuxBlinkEnabled = e.target.checked;
    applyThemeSettings();
    saveSettings();
  });

  // 文件选择：背景图
  const bgFileInput = document.getElementById('bgFileInput');
  bgFileInput.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      const url = URL.createObjectURL(file);
      document.getElementById('bgUrlInput').value = url;
      setCustomBackground(url);
      saveSettings();
    }
  });

  // 文件选择：启用音效
  const soundFileEnable = document.getElementById('soundFileEnable');
  soundFileEnable.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      const url = URL.createObjectURL(file);
      document.getElementById('soundUrlEnable').value = url;
      enableSoundUrl = url;
      audioEnable = new Audio(url);
      saveSettings();
    }
  });

  // 文件选择：禁用音效
  const soundFileDisable = document.getElementById('soundFileDisable');
  soundFileDisable.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      const url = URL.createObjectURL(file);
      document.getElementById('soundUrlDisable').value = url;
      disableSoundUrl = url;
      audioDisable = new Audio(url);
      saveSettings();
    }
  });

  // 初次加载后移除 iOS 模糊动画类
  setTimeout(() => {
    document.body.classList.remove('ios-blur-anim');
  }, 800);
});

function connectWebSocket() {
  ws = new WebSocket('ws://localhost:8081');
  ws.onopen = () => {
    ws.send(JSON.stringify({ action: 'getModules' }));
  };
  ws.onmessage = e => {
    const msg = JSON.parse(e.data);
    if (msg.action === 'modulesStatus') {
      modulesData = msg.modules || [];
      if (currentCategory && !modulesData.some(m => m.category === currentCategory)) {
        currentCategory = null;
        document.getElementById('islandMainBtn').textContent = '选择分类';
      }
      animateModulesEnter();
      renderModules();
    }
  };
  ws.onerror = () => {};
  ws.onclose = () => {
    setTimeout(connectWebSocket, 3000);
  };
}

function loadSettings() {
  const raw = localStorage.getItem('myWebAppSettings');
  if (!raw) return;
  try {
    const st = JSON.parse(raw);
    if (st.darkMode) {
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');
    } else {
      document.body.classList.add('light-mode');
      document.body.classList.remove('dark-mode');
    }
    if (typeof st.sound === 'boolean') {
      soundEnabled = st.sound;
      document.getElementById('soundEnableCheckbox').checked = st.sound;
    }
    if (st.soundUrlEnable) {
      enableSoundUrl = st.soundUrlEnable;
      document.getElementById('soundUrlEnable').value = enableSoundUrl;
      audioEnable = new Audio(enableSoundUrl);
    }
    if (st.soundUrlDisable) {
      disableSoundUrl = st.soundUrlDisable;
      document.getElementById('soundUrlDisable').value = disableSoundUrl;
      audioDisable = new Audio(disableSoundUrl);
    }
    if (st.bgUrl) {
      document.body.style.backgroundImage = st.bgUrl;
    }
    if (st.layout) {
      document.getElementById('layoutSelector').value = st.layout;
      applyLayout(st.layout);
    }
    if (st.style) {
      document.getElementById('styleSelector').value = st.style;
      const link = document.getElementById('dynamic-styles');
      if (st.style) link.href = 'styles/' + st.style;
    }
    if (st.currentCategory) {
      currentCategory = st.currentCategory;
      document.getElementById('islandMainBtn').textContent = currentCategory;
    }
    if (typeof st.iosBlurEnabled === 'boolean') {
      iosBlurEnabled = st.iosBlurEnabled;
      document.getElementById('iosBlurCheckbox').checked = iosBlurEnabled;
    }
    if (typeof st.win98AnimEnabled === 'boolean') {
      win98AnimEnabled = st.win98AnimEnabled;
      document.getElementById('win98AnimCheckbox').checked = win98AnimEnabled;
    }
    if (typeof st.linuxBlinkEnabled === 'boolean') {
      linuxBlinkEnabled = st.linuxBlinkEnabled;
      document.getElementById('linuxTerminalBlinkCheckbox').checked = linuxBlinkEnabled;
    }
  } catch (e) {
    console.error("加载设置失败：", e);
  }
  applyThemeSettings();
}

function saveSettings() {
  const settings = {
    darkMode: document.body.classList.contains('dark-mode'),
    sound: soundEnabled,
    soundUrlEnable: enableSoundUrl,
    soundUrlDisable: disableSoundUrl,
    bgUrl: document.body.style.backgroundImage || '',
    layout: document.getElementById('layoutSelector')?.value || 'grid',
    style: document.getElementById('styleSelector')?.value || '',
    currentCategory,
    iosBlurEnabled,
    iosCardBlurDisabled,
    win98AnimEnabled,
    linuxBlinkEnabled
  };
  localStorage.setItem('myWebAppSettings', JSON.stringify(settings));
}

function initStyleSelector() {
  const styleSel = document.getElementById('styleSelector');
  if (!styleSel) return;
  const themes = [
    { file: '', label: 'Default(无)' },
    { file: 'win98.css', label: 'Win98/XP' },
    { file: 'ios.css', label: 'iOS毛玻璃' },
    { file: 'linux.css', label: 'Linux风格' }
  ];
  styleSel.innerHTML = '';
  themes.forEach(th => {
    const opt = document.createElement('option');
    opt.value = th.file;
    opt.textContent = th.label;
    styleSel.appendChild(opt);
  });
  styleSel.value = '';
  styleSel.addEventListener('change', () => {
    const val = styleSel.value;
    const link = document.getElementById('dynamic-styles');
    link.href = val ? 'styles/' + val : '';
    document.getElementById('iosBlurRow').style.display = (val === 'ios.css') ? 'flex' : 'none';
    document.getElementById('win98SetRow').style.display = (val === 'win98.css') ? 'flex' : 'none';
    document.getElementById('linuxSetRow').style.display = (val === 'linux.css') ? 'flex' : 'none';
    saveSettings();
    applyThemeSettings();
  });
}

function applyThemeSettings() {
  const styleVal = document.getElementById('styleSelector').value;

  if (styleVal === 'ios.css') {
    document.body.classList.toggle('ios-blur-disabled', !iosBlurEnabled);
    document.body.classList.toggle('ios-card-noblur', iosCardBlurDisabled); // 控制是否禁用卡片模糊
  } else {
    document.body.classList.remove('ios-blur-disabled');
    document.body.classList.remove('ios-card-noblur');
  }

  if (styleVal === 'win98.css') {
    document.body.classList.toggle('win98-anim-off', !win98AnimEnabled);
  } else {
    document.body.classList.remove('win98-anim-off');
  }

  if (styleVal === 'linux.css') {
    document.body.classList.toggle('linux-blink-cursor', linuxBlinkEnabled);
  } else {
    document.body.classList.remove('linux-blink-cursor');
  }
}


function renderCategoryIsland() {
  const cats = [...new Set(modulesData.map(m => m.category))];
  const menu = document.getElementById('islandMenu');
  menu.innerHTML = '';
  cats.forEach(cat => {
    const btn = document.createElement('button');
    btn.className = 'island-cat-btn';
    btn.textContent = cat;
    btn.addEventListener('click', () => {
      currentCategory = cat;
      document.getElementById('islandMainBtn').textContent = cat;
      animateModulesEnter();
      renderModules();
      saveSettings();
    });
    menu.appendChild(btn);
  });
}

function renderModules() {
  renderCategoryIsland();
  const container = document.getElementById('modulesContainer');
  container.innerHTML = '';
  if (!modulesData.length || !currentCategory) return;
  const searchVal = (document.getElementById('searchInput').value || '').toLowerCase();
  const matched = modulesData.filter(m => {
    if (m.category !== currentCategory) return false;
    return searchVal ? (m.name.toLowerCase().includes(searchVal) || (m.description || '').toLowerCase().includes(searchVal)) : true;
  });
  matched.forEach(mod => {
    const card = document.createElement('div');
    card.className = 'module-card';
    card.addEventListener('click', e => {
      if (e.target === card) card.classList.toggle('selected');
    });
    const header = document.createElement('div');
    header.className = 'module-header';
    const title = document.createElement('span');
    title.className = 'module-name';
    title.textContent = mod.name;
    const switchLabel = document.createElement('label');
    switchLabel.className = 'ios-switch ios-switch-mini';
    const switchInput = document.createElement('input');
    switchInput.type = 'checkbox';
    switchInput.checked = !!mod.enabled;
    switchInput.addEventListener('change', () => toggleModule(mod, switchInput.checked));
    const slider = document.createElement('span');
    slider.className = 'slider';
    switchLabel.appendChild(switchInput);
    switchLabel.appendChild(slider);
    header.appendChild(title);
    header.appendChild(switchLabel);
    card.appendChild(header);
    const desc = document.createElement('div');
    desc.className = 'module-description';
    desc.textContent = mod.description || '';
    card.appendChild(desc);
    if (mod.values && mod.values.length) {
      const paramDiv = document.createElement('div');
      paramDiv.className = 'module-params';
      mod.values.forEach(val => {
        const row = document.createElement('div');
        row.className = 'param-row';
        const lbl = document.createElement('label');
        lbl.textContent = val.name + ':';
        row.appendChild(lbl);
        if (val.type === 'boolean') {
          const boolLabel = document.createElement('label');
          boolLabel.className = 'ios-switch ios-switch-mini';
          const boolInput = document.createElement('input');
          boolInput.type = 'checkbox';
          boolInput.checked = (val.value === 'true');
          boolInput.addEventListener('change', () => sendSetValue(mod, val.name, boolInput.checked.toString()));
          const boolSlider = document.createElement('span');
          boolSlider.className = 'slider';
          boolLabel.appendChild(boolInput);
          boolLabel.appendChild(boolSlider);
          row.appendChild(boolLabel);
        } else if (val.type === 'number') {
          const numberContainer = document.createElement('div');
          numberContainer.className = 'number-input-container';
          const rangeInput = document.createElement('input');
          rangeInput.type = 'range';
          rangeInput.min = '0';
          rangeInput.max = '100';
          rangeInput.value = val.value || '0';
          rangeInput.className = 'param-range';
          const numInput = document.createElement('input');
          numInput.type = 'number';
          numInput.value = val.value || '0';
          numInput.className = 'param-input';
          rangeInput.addEventListener('input', () => {
            numInput.value = rangeInput.value;
          });
          rangeInput.addEventListener('change', () => sendSetValue(mod, val.name, rangeInput.value));
          numInput.addEventListener('input', () => {
            rangeInput.value = numInput.value;
          });
          numInput.addEventListener('change', () => sendSetValue(mod, val.name, numInput.value));
          numberContainer.appendChild(rangeInput);
          numberContainer.appendChild(numInput);
          row.appendChild(numberContainer);
        } else {
          const txt = document.createElement('input');
          txt.type = 'text';
          txt.className = 'param-input';
          txt.value = val.value || '';
          txt.addEventListener('change', e => sendSetValue(mod, val.name, e.target.value));
          row.appendChild(txt);
        }
        paramDiv.appendChild(row);
      });
      card.appendChild(paramDiv);
    }
    const ctrlDiv = document.createElement('div');
    ctrlDiv.className = 'module-controls';
    const bindBtn = document.createElement('button');
    bindBtn.className = 'btn-secondary btn-keybind';
    bindBtn.textContent = (mod.keybind && mod.keybind !== -1) ? `BIND(${mapKeyBindToString(mod.keybind)})` : 'BIND';
    bindBtn.addEventListener('click', e => {
      e.stopPropagation();
      openBindPopup(mod);
    });
    ctrlDiv.appendChild(bindBtn);
    card.appendChild(ctrlDiv);
    container.appendChild(card);
  });
}

function toggleModule(mod, newState) {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      action: 'toggleModule',
      moduleName: mod.name,
      enabled: newState
    }));
  }
  if (soundEnabled) {
    if (newState && audioEnable) {
      audioEnable.currentTime = 0;
      audioEnable.play().catch(() => {});
    } else if (!newState && audioDisable) {
      audioDisable.currentTime = 0;
      audioDisable.play().catch(() => {});
    }
  }
  showMessage(`模块 "${mod.name}" 已${newState ? '启用' : '关闭'}`);
  saveSettings();
}

function sendSetValue(mod, valueName, newVal) {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({
      action: 'setValue',
      moduleName: mod.name,
      valueName,
      newValue: newVal
    }));
  }
  showMessage(`更新 ${mod.name}.${valueName} => ${newVal}`);
  saveSettings();
}

function initBindPopup() {
  const bindInput = document.getElementById('bindInput');
  const confirmBtn = document.getElementById('bindConfirmBtn');
  const cancelBtn = document.getElementById('bindCancelBtn');
  const unbindBtn = document.getElementById('bindUnbindBtn');
  bindInput.addEventListener('keydown', e => {
    e.preventDefault();
    bindInput.value = e.key;
    bindInput.dataset.key = e.key;
  });
  confirmBtn.addEventListener('click', () => {
    if (!bindPopupModule) return;
    const rawKey = bindInput.dataset.key;
    if (!rawKey) {
      showMessage('请先按下需要绑定的快捷键');
      return;
    }
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({
        action: 'bindKey',
        moduleName: bindPopupModule.name,
        key: rawKey
      }));
    }
    showMessage(`模块 "${bindPopupModule.name}" 已绑定 => ${rawKey}`);
    closeBindPopup();
  });
  cancelBtn.addEventListener('click', closeBindPopup);
  unbindBtn.addEventListener('click', () => {
    if (!bindPopupModule) return;
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({
        action: 'unbindKey',
        moduleName: bindPopupModule.name
      }));
    }
    showMessage(`模块 "${bindPopupModule.name}" 已解绑`);
    closeBindPopup();
  });
}

function openBindPopup(mod) {
  bindPopupModule = mod;
  document.getElementById('bindPopupTitle').textContent = `绑定快捷键: ${mod.name}`;
  let desc = '尚未绑定按键';
  if (mod.keybind && mod.keybind !== -1) {
    desc = `当前已绑定: ${mapKeyBindToString(mod.keybind)}`;
  }
  document.getElementById('bindPopupDesc').textContent = desc;
  document.getElementById('bindPopup').classList.add('active');
  const bindInput = document.getElementById('bindInput');
  bindInput.value = '';
  delete bindInput.dataset.key;
  bindInput.focus();
}

function closeBindPopup() {
  bindPopupModule = null;
  document.getElementById('bindPopup').classList.remove('active');
}

function mapKeyBindToString(k) {
  if (typeof k !== 'number') return '???';
  if (k >= 65 && k <= 90) return String.fromCharCode(k);
  if (k === 13) return 'ENTER';
  if (k === 32) return 'SPACE';
  return 'KEY' + k;
}

function showMessage(txt) {
  const box = document.getElementById('messageBox');
  box.textContent = txt;
  box.classList.add('show');
  if (messageTimer) clearTimeout(messageTimer);
  messageTimer = setTimeout(() => {
    box.classList.remove('show');
  }, 3000);
}

function openSettings(open) {
  const panel = document.getElementById('settingsPanel');
  if (open) {
    panel.classList.add('active');
    document.getElementById('darkModeCheckbox').checked = document.body.classList.contains('dark-mode');
    const val = document.getElementById('styleSelector').value;
    document.getElementById('iosBlurRow').style.display = (val === 'ios.css') ? 'flex' : 'none';
    document.getElementById('win98SetRow').style.display = (val === 'win98.css') ? 'flex' : 'none';
    document.getElementById('linuxSetRow').style.display = (val === 'linux.css') ? 'flex' : 'none';
  } else {
    panel.classList.remove('active');
  }
}

function setDarkMode(enable) {
  if (enable) {
    document.body.classList.add('dark-mode');
    document.body.classList.remove('light-mode');
  } else {
    document.body.classList.add('light-mode');
    document.body.classList.remove('dark-mode');
  }
  saveSettings();
}

function setCustomBackground(url) {
  if (url) {
    document.body.style.backgroundImage = `url("${url}")`;
    document.body.style.backgroundSize = 'cover';
    document.body.classList.add('custom-bg');
  } else {
    document.body.style.backgroundImage = '';
    document.body.classList.remove('custom-bg');
  }
  saveSettings();
}

function applyLayout(layoutMode) {
  const container = document.getElementById('modulesContainer');
  container.classList.remove('modules-grid', 'modules-center', 'modules-edge');
  if (layoutMode === 'center') {
    container.classList.add('modules-center');
  } else if (layoutMode === 'edge') {
    container.classList.add('modules-edge');
  } else {
    container.classList.add('modules-grid');
  }
}

function animateModulesEnter() {
  const container = document.getElementById('modulesContainer');
  container.classList.remove('modules-enter');
  void container.offsetWidth;
  container.classList.add('modules-enter');
}
