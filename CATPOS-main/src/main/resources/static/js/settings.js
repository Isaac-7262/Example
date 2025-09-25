// src/main/resources/static/js/settings.js

document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication().then(isAuthenticated => {
        if (isAuthenticated) {
            initializeSettingsPage();
        } else {
            redirectToLogin();
        }
    });
});

function initializeSettingsPage() {
    loadSettings();
    bindSettingsActions();
    updateSearchPlaceholder('settings');
}

function bindSettingsActions() {
    $('#btnSaveSettings').addEventListener('click', saveSettings);
}

function updateSearchPlaceholder(sectionId) {
    const input = $('#searchInput');
    const header = $('.header');
    if (input) {
        input.placeholder = 'ค้นหา...'; // Generic search for settings
    }
    if (header) {
        header.classList.add('hidden'); // Hide header for settings section
    }
}

// --- Settings ---
async function loadSettings() {
    try {
        const r = await fetch(API.settings);
        settingsCache = r.ok ? await r.json() : {};
        $('#shopName').value = settingsCache.shopName || '';
        $('#shopAddress').value = settingsCache.address || '';
        $('#shopPhone').value = settingsCache.phone || '';
        $('#taxId').value = settingsCache.taxId || '';
        $('#promptpayId').value = settingsCache.promptpayId || '';
    } catch (err) { console.error('Error loading settings:', err); }
}

async function saveSettings() {
    const payload = {
        id: 1, // Assuming a single settings entry with ID 1
        shopName: $('#shopName').value.trim(),
        address: $('#shopAddress').value.trim(),
        phone: $('#shopPhone').value.trim(),
        taxId: $('#taxId').value.trim(),
        promptpayId: $('#promptpayId').value.trim()
    };
    try {
        const r = await fetch(API.settings, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
        if (!r.ok) throw new Error('Save failed');
        settingsCache = await r.json();
        toast('บันทึกการตั้งค่าแล้ว');
    } catch (err) { toast('บันทึกไม่สำเร็จ'); }
}
