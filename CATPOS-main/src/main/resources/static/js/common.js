// src/main/resources/static/js/common.js

const API = {
    products: '/api/products',
    customers: '/api/customers',
    orders: '/api/orders',
    report: '/api/orders/report',
    settings: '/api/settings',
    auth: '/api/auth',
    loyalty: '/api/loyalty',
    cart: '/api/cart',
    payment: '/api/payment',
    uploads: '/api/uploads'
};

let settingsCache = {};
let currentUser = null;
let sessionToken = null;

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);
const fmtBaht = (n) => Number(n || 0).toLocaleString('th-TH', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const esc = (s) => (s ?? '').toString().replace(/[&<>/"'`=]/g, m => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': '&quot;', "'": "&#39;", "/": "&#x2F;", "`": "&#x60;", "=": "&#x3D;" }[m]));

const showModal = (id) => $(`#${id}`)?.classList.add('show');
const closeModal = (id) => $(`#${id}`)?.classList.remove('show');
const toast = (msg) => alert(msg);

document.addEventListener('DOMContentLoaded', () => {
    $('#currentDate').textContent = new Date().toLocaleDateString('th-TH', { year: 'numeric', month: '2-digit', day: '2-digit' });
    
    // This setup is for pages that have modals.
    $$('.modal').forEach(m => m.addEventListener('click', e => {
        if (e.target === m) closeModal(m.id);
    }));
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            $$('.modal.show').forEach(m => closeModal(m.id));
        }
    });
});

// --- Authentication --- //
async function checkAuthentication() {
    sessionToken = localStorage.getItem('sessionToken');
    const storedUser = localStorage.getItem('currentUser');

    if (!sessionToken || !storedUser) {
        return false;
    }

    try {
        const response = await fetch(`${API.auth}/validate`, {
            headers: { 'Authorization': sessionToken }
        });
        const result = await response.json();

        if (result.valid) {
            currentUser = JSON.parse(storedUser);
            updateUserDisplay();
            return true;
        } else {
            clearAuthData();
            return false;
        }
    } catch (error) {
        console.error('Authentication check failed:', error);
        clearAuthData();
        return false;
    }
}

function updateUserDisplay() {
    if (currentUser) {
        const roleText = currentUser.role === 'ADMIN' ? 'ผู้ดูแลระบบ' :
                         currentUser.role === 'MANAGER' ? 'ผู้จัดการ' : 'พนักงานขาย';
        const display = $('#currentUserDisplay');
        if(display) display.textContent = `👤 ${roleText}: ${currentUser.name}`;
    }
}

function redirectToLogin() {
    window.location.href = '/login.html';
}

function clearAuthData() {
    localStorage.removeItem('sessionToken');
    localStorage.removeItem('currentUser');
    sessionToken = null;
    currentUser = null;
}

async function logout() {
    if (confirm('ต้องการออกจากระบบใช่หรือไม่?')) {
        try {
            if (sessionToken) {
                await fetch(`${API.auth}/logout`, {
                    method: 'POST',
                    headers: { 'Authorization': sessionToken }
                });
            }
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            clearAuthData();
            redirectToLogin();
        }
    }
}

// Add Authorization header to all API requests
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    if (url.startsWith('/api/') && sessionToken && !url.includes('/auth/login') && !url.includes('/auth/validate')) {
        options.headers = {
            ...options.headers,
            'Authorization': sessionToken
        };
    }
    return originalFetch(url, options);
};