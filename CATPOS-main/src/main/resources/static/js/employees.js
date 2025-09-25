document.addEventListener('DOMContentLoaded', () => {
    if (typeof checkAuthentication !== 'function') {
        console.error('common.js not loaded or checkAuthentication function is missing');
        return;
    }

    checkAuthentication().then(isAuthenticated => {
        if (isAuthenticated) {
            initializeEmployeesPage();
        } else {
            redirectToLogin();
        }
    });
});

function initializeEmployeesPage() {
    if (!currentUser || currentUser.role !== 'ADMIN') {
        console.warn('Access denied: User is not an ADMIN.', currentUser);
        document.body.innerHTML = '<div style="text-align:center; padding: 50px; font-size: 1.2em;">🚫 คุณไม่มีสิทธิ์เข้าถึงหน้านี้</div>';
        return;
    }
    
    loadEmployees();
    bindEmployeeActions();
}

async function loadEmployees(searchTerm = '') {
    if (!API.auth) {
        console.error('API.auth is not defined. Check common.js loading.');
        toast('เกิดข้อผิดพลาด: API ไม่พร้อมใช้งาน');
        return;
    }
    try {
        const response = await fetch(`${API.auth}/users`);
        if (!response.ok) {
            throw new Error('Failed to load employees');
        }
        let employees = await response.json();
        renderEmployeesTable(employees, searchTerm);
    } catch (error) {
        console.error('Error loading employees:', error);
        const tableBody = document.getElementById('employeesTable');
        if(tableBody) tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">ไม่สามารถโหลดข้อมูลพนักงานได้</td></tr>';
    }
}

async function saveEmployee(event) {
    event.preventDefault();
    if (!API.auth) {
        console.error('API.auth is not defined. Check common.js loading.');
        toast('เกิดข้อผิดพลาด: API ไม่พร้อมใช้งาน');
        return;
    }
    const form = event.target;
    const id = form.dataset.id;
    const password = document.getElementById('employeePassword').value;

    const payload = {
        username: document.getElementById('employeeUsername').value.trim(),
        fullName: document.getElementById('employeeFullName').value.trim(),
        role: document.getElementById('employeeRole').value,
    };

    if (id) {
        payload.id = id;
    }

    if (password) {
        payload.password = password;
    }

    if (!id && !password) {
        toast('กรุณากำหนดรหัสผ่านสำหรับพนักงานใหม่');
        return;
    }

    const url = id ? `${API.auth}/users/${id}` : `${API.auth}/register`;
    const method = id ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'บันทึกข้อมูลไม่สำเร็จ');
        }

        toast('บันทึกข้อมูลพนักงานสำเร็จ');
        closeModal('employeeModal');
        loadEmployees();
    } catch (error) {
        console.error('Failed to save employee:', error);
        toast(`เกิดข้อผิดพลาด: ${error.message}`);
    }
}

async function editEmployee(id) {
    if (!API.auth) {
        console.error('API.auth is not defined. Check common.js loading.');
        toast('เกิดข้อผิดพลาด: API ไม่พร้อมใช้งาน');
        return;
    }
    try {
        const response = await fetch(`${API.auth}/users/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch employee data');
        }
        const employee = await response.json();
        openEmployeeModal(employee);
    } catch (error) {
        console.error('Error fetching employee for edit:', error);
        toast('ไม่สามารถดึงข้อมูลพนักงานได้');
    }
}

async function deleteEmployee(id) {
    if (!API.auth) {
        console.error('API.auth is not defined. Check common.js loading.');
        toast('เกิดข้อผิดพลาด: API ไม่พร้อมใช้งาน');
        return;
    }
    if (currentUser && currentUser.id === id) {
        toast('คุณไม่สามารถลบบัญชีของตัวเองได้');
        return;
    }

    if (!confirm('คุณแน่ใจหรือไม่ว่าต้องการลบพนักงานคนนี้?')) {
        return;
    }

    try {
        const response = await fetch(`${API.auth}/users/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Failed to delete employee');
        }

        toast('ลบพนักงานสำเร็จ');
        loadEmployees();
    } catch (error) {
        console.error('Error deleting employee:', error);
        toast('ลบพนักงานไม่สำเร็จ');
    }
}

function bindEmployeeActions() {
    const btnAdd = document.getElementById('btnAddEmployee');
    if (btnAdd) {
        btnAdd.addEventListener('click', () => openEmployeeModal());
    }

    const form = document.getElementById('employeeForm');
    if (form) {
        form.addEventListener('submit', saveEmployee);
    }

    const searchInput = document.getElementById('searchInput');
    if(searchInput) {
        searchInput.addEventListener('input', (e) => loadEmployees(e.target.value));
    }
}

async function loadEmployees(searchTerm = '') {
    try {
        const response = await fetch('/api/employees');
        if (!response.ok) {
            throw new Error('Failed to load employees');
        }
        let employees = await response.json();
        renderEmployeesTable(employees, searchTerm);
    } catch (error) {
        console.error('Error loading employees:', error);
        const tableBody = document.getElementById('employeesTable');
        if(tableBody) tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">ไม่สามารถโหลดข้อมูลพนักงานได้</td></tr>';
    }
}

function renderEmployeesTable(employees, searchTerm = '') {
    const tableBody = document.getElementById('employeesTable');
    if (!tableBody) return;

    const lowercasedFilter = searchTerm.toLowerCase();
    const filteredEmployees = employees.filter(emp => {
        return (emp.username.toLowerCase().includes(lowercasedFilter) || 
                emp.name.toLowerCase().includes(lowercasedFilter));
    });

    if (filteredEmployees.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center;">ไม่พบข้อมูลพนักงาน</td></tr>';
        return;
    }

    tableBody.innerHTML = filteredEmployees.map(emp => {
        const roleText = emp.role === 'ADMIN' ? 'ผู้ดูแลระบบ' : emp.role === 'MANAGER' ? 'ผู้จัดการ' : 'พนักงานขาย';
        return `
            <tr>
                <td>${emp.id}</td>
                <td>${esc(emp.username)}</td>
                <td>${esc(emp.name)}</td>
                <td>${roleText}</td>
                <td>
                    <button class="btn btn-secondary" onclick="editEmployee(${emp.id})">แก้ไข</button>
                    <button class="btn btn-danger" onclick="deleteEmployee(${emp.id})" ${currentUser.id === emp.id ? 'disabled' : ''}>ลบ</button>
                </td>
            </tr>
        `;
    }).join('');
}

function openEmployeeModal(employee = null) {
    const modalTitle = document.getElementById('employeeModalTitle');
    const form = document.getElementById('employeeForm');
    const passwordField = document.getElementById('employeePassword');

    if (modalTitle) modalTitle.textContent = employee ? 'แก้ไขข้อมูลพนักงาน' : 'เพิ่มพนักงานใหม่';
    if (form) form.dataset.id = employee ? employee.id : '';
    document.getElementById('employeeId').value = employee ? employee.id : '';
    document.getElementById('employeeUsername').value = employee ? employee.username : '';
    document.getElementById('employeeFullName').value = employee ? employee.name : '';
    document.getElementById('employeeRole').value = employee ? employee.role : 'STAFF';
    if(passwordField) {
        passwordField.value = '';
        passwordField.placeholder = employee ? 'เว้นว่างไว้หากไม่ต้องการเปลี่ยน' : 'กรอกรหัสผ่าน';
        passwordField.required = !employee;
    }
    showModal('employeeModal');
}

async function saveEmployee(event) {
    event.preventDefault();
    const form = event.target;
    const id = form.dataset.id;
    const password = document.getElementById('employeePassword').value;
    const payload = {
        username: document.getElementById('employeeUsername').value.trim(),
        name: document.getElementById('employeeFullName').value.trim(),
        role: document.getElementById('employeeRole').value,
        password: password
    };
    if (!id && !password) {
        toast('กรุณากำหนดรหัสผ่านสำหรับพนักงานใหม่');
        return;
    }
    const url = id ? `/api/employees/${id}` : '/api/employees';
    const method = id ? 'PUT' : 'POST';
    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'บันทึกข้อมูลไม่สำเร็จ');
        }
        toast('บันทึกข้อมูลพนักงานสำเร็จ');
        closeModal('employeeModal');
        loadEmployees();
    } catch (error) {
        console.error('Failed to save employee:', error);
        toast(`เกิดข้อผิดพลาด: ${error.message}`);
    }
}

async function editEmployee(id) {
    try {
        const response = await fetch(`/api/employees/${id}`);
        if (!response.ok) {
            throw new Error('Failed to fetch employee data');
        }
        const employee = await response.json();
        openEmployeeModal(employee);
    } catch (error) {
        console.error('Error fetching employee for edit:', error);
        toast('ไม่สามารถดึงข้อมูลพนักงานได้');
    }
}

async function deleteEmployee(id) {
    if (currentUser && currentUser.id === id) {
        toast('คุณไม่สามารถลบบัญชีของตัวเองได้');
        return;
    }
    if (!confirm('คุณแน่ใจหรือไม่ว่าต้องการลบพนักงานคนนี้?')) {
        return;
    }
    try {
        const response = await fetch(`/api/employees/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete employee');
        }
        toast('ลบพนักงานสำเร็จ');
        loadEmployees();
    } catch (error) {
        console.error('Error deleting employee:', error);
        toast('ลบพนักงานไม่สำเร็จ');
    }
}