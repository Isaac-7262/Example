// src/main/resources/static/js/customers.js

let customers = [];

document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication().then(isAuthenticated => {
        if (isAuthenticated) {
            initializeCustomersPage();
        } else {
            redirectToLogin();
        }
    });
});

function initializeCustomersPage() {
    loadCustomers();
    bindCustomerActions();
    updateSearchPlaceholder('customers');
}

function bindCustomerActions() {
    $('#btnAddCustomer').addEventListener('click', () => openCustomerModal());
    $('#customerForm').addEventListener('submit', saveCustomer);
    $('#searchInput').addEventListener('input', e => handleGlobalSearch(e.target.value));
}

function updateSearchPlaceholder(sectionId) {
    const input = $('#searchInput');
    const header = $('.header');
    if (input) {
        input.placeholder = '🔍 ค้นหาลูกค้า...';
    }
    if (header) {
        header.classList.remove('hidden');
    }
}

function handleGlobalSearch(rawTerm) {
    const term = (rawTerm || '').trim().toLowerCase();
    renderCustomersTable(term);
}

// --- Customers ---
async function loadCustomers() {
    try {
        const r = await fetch(API.customers, { cache: 'no-store' });
        customers = r.ok ? await r.json() : [];
        renderCustomersTable();
    } catch (err) { console.error('Error loading customers:', err); }
}

function renderCustomersTable(term = '') {
    const filter = (term || '').toLowerCase();
    const list = filter ? customers.filter(c => {
        const name = (c.name || '').toLowerCase();
        const phone = (c.phone || '').toLowerCase();
        const email = (c.email || '').toLowerCase();
        const address = (c.address || '').toLowerCase();
        const code = `c${String(c.id).padStart(3, '0')}`;
        return name.includes(filter) || phone.includes(filter) || email.includes(filter) || address.includes(filter) || code.includes(filter);
    }) : customers;

    $('#customersTable').innerHTML = list.map(c => `
        <tr>
          <td>C${String(c.id).padStart(3, '0')}</td>
          <td>${esc(c.name || '')}</td>
          <td>${esc(c.phone || '')}</td>
          <td>${esc(c.email || '')}</td>
          <td>${esc(c.address || '')}</td>
          <td>${esc(String(c.loyaltyPoints ?? 0))}</td>
          <td>
            <button class="btn btn-secondary" onclick="editCustomer(${c.id})">แก้ไข</button>
            <button class="btn btn-danger" onclick="deleteCustomer(${c.id})">ลบ</button>
          </td>
        </tr>`).join('');
}

function openCustomerModal(c = null) {
    $('#customerModalTitle').textContent = c ? '✏️ แก้ไขข้อมูลลูกค้า' : '➕ เพิ่มลูกค้าใหม่';
    $('#customerForm').dataset.id = c?.id || '';
    $('#customerName').value = c?.name || '';
    $('#customerPhone').value = c?.phone || '';
    $('#customerEmail').value = c?.email || '';
    $('#customerAddress').value = c?.address || '';
    showModal('customerModal');
}
window.editCustomer = (id) => openCustomerModal(customers.find(x => x.id === id)); // Make it globally accessible for onclick

async function saveCustomer(e) {
    e.preventDefault();
    const id = $('#customerForm').dataset.id;
    const payload = {
        name: $('#customerName').value.trim(),
        phone: $('#customerPhone').value.trim(),
        email: $('#customerEmail').value.trim(),
        address: $('#customerAddress').value.trim()
    };
    try {
        const r = await fetch(id ? `${API.customers}/${id}` : API.customers, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!r.ok) throw new Error('Save customer failed');
        closeModal('customerModal');
        await loadCustomers();
    } catch (err) { toast('บันทึกลูกค้าไม่สำเร็จ'); }
}

window.deleteCustomer = async (id) => {
    if (!confirm('ต้องการลบข้อมูลลูกค้านี้ใช่หรือไม่?')) return;
    try {
        const r = await fetch(`${API.customers}/${id}`, { method: 'DELETE' });
        if (r.ok) await loadCustomers();
        else toast('ลบข้อมูลลูกค้าไม่สำเร็จ');
    } catch (err) { toast('เกิดข้อผิดพลาดในการลบข้อมูลลูกค้า'); }
}
