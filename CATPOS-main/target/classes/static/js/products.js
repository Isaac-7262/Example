// src/main/resources/static/js/products.js

let products = [];
let productImageFile = null;

const DEFAULT_PRODUCT_IMAGE = 'https://via.placeholder.com/160x120?text=No+Image';
const CATEGORY_LABELS = {
    drinks: '🥤 เครื่องดื่ม',
    desserts: '🍰 เบเกอรี่ & ขนมหวาน',
    snacks: '🍲 อาหารทานเล่น',
    others: '📦 อื่น ๆ',
    'อื่น ๆ': '📦 อื่น ๆ'
};
const getCatName = (c) => CATEGORY_LABELS[c] || CATEGORY_LABELS.others;

document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication().then(isAuthenticated => {
        if (isAuthenticated) {
            initializeProductsPage();
        } else {
            redirectToLogin();
        }
    });
});

function initializeProductsPage() {
    loadProducts();
    bindProductActions();
    updateSearchPlaceholder('products');
}

function bindProductActions() {
    $('#btnAddProduct').addEventListener('click', () => openProductModal());
    $('#productForm').addEventListener('submit', saveProduct);
    $('#searchInput').addEventListener('input', e => handleGlobalSearch(e.target.value));

    const productImageInput = $('#productImageFile');
    if (productImageInput) {
        productImageInput.addEventListener('change', onProductImageChange);
    }
    const clearImageBtn = $('#btnClearProductImage');
    if (clearImageBtn) {
        clearImageBtn.addEventListener('click', clearProductImageSelection);
    }
}

function updateSearchPlaceholder(sectionId) {
    const input = $('#searchInput');
    const header = $('.header');
    if (input) {
        input.placeholder = '🔍 ค้นหาเมนู...';
    }
    if (header) {
        header.classList.remove('hidden');
    }
}

function handleGlobalSearch(rawTerm) {
    const term = (rawTerm || '').trim().toLowerCase();
    renderProductsTable(term);
}

// --- Products ---
async function loadProducts() {
    try {
        const r = await fetch(API.products);
        products = r.ok ? await r.json() : [];
        renderProductsTable();
    } catch (err) { console.error('Error loading products:', err); }
}

function renderProductsTable(term = '') {
    const filter = (term || '').toLowerCase();
    const list = filter ? products.filter(p => {
        const name = (p.name || '').toLowerCase();
        const code = (p.code || '').toLowerCase();
        const category = getCatName(p.category).toLowerCase();
        return name.includes(filter) || code.includes(filter) || category.includes(filter);
    }) : products;

    $('#productsTable').innerHTML = list.map(p => `
      <tr>
        <td>${esc(p.code || '')}</td>
        <td><div class="table-product-cell"><img src="${esc(p.imageUrl || 'https://via.placeholder.com/48x48?text=No')}" alt="${esc(p.name)}" class="table-product-image" onerror="this.src='https://via.placeholder.com/48x48?text=No';"> ${esc(p.name)}</div></td>
        <td>฿${fmtBaht(p.price)}</td>
        <td>${p.stock ?? 0}</td>
        <td>${esc(getCatName(p.category))}</td>
        <td>
          <button class="btn btn-secondary" onclick="editProduct(${p.id})">แก้ไข</button>
          <button class="btn btn-danger" onclick="deleteProduct(${p.id})">ลบ</button>
        </td>
      </tr>`).join('');
}

function onProductImageChange(e) {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) {
        toast('ไฟล์มีขนาดใหญ่เกิน 5 MB');
        e.target.value = '';
        return;
    }
    productImageFile = file;
    const preview = $('#productImagePreview');
    if (preview) {
        preview.src = URL.createObjectURL(file);
    }
}

function clearProductImageSelection() {
    productImageFile = null;
    const fileInput = $('#productImageFile');
    if (fileInput) fileInput.value = '';
    const urlInput = $('#productImageUrl');
    if (urlInput) urlInput.value = '';
    const preview = $('#productImagePreview');
    if (preview) preview.src = DEFAULT_PRODUCT_IMAGE;
}

function openProductModal(p = null) {
    $('#productModalTitle').textContent = p ? '✏️ แก้ไขเมนู' : '➕ เพิ่มเมนูใหม่';
    $('#productForm').dataset.id = p?.id || '';
    $('#productCode').value = p?.code || '';
    $('#productName').value = p?.name || '';
    $('#productPrice').value = p?.price ?? '';
    $('#productStock').value = p?.stock ?? '';
    $('#productCategory').value = p?.category || 'drinks';
    productImageFile = null;
    const url = p?.imageUrl || '';
    $('#productImageUrl').value = url;
    const fileInput = $('#productImageFile');
    if (fileInput) fileInput.value = '';
    const preview = $('#productImagePreview');
    if (preview) preview.src = url || DEFAULT_PRODUCT_IMAGE;
    showModal('productModal');
}
window.editProduct = (id) => openProductModal(products.find(x => x.id === id)); // Make it globally accessible for onclick

async function saveProduct(e) {
    e.preventDefault();
    const id = $('#productForm').dataset.id;
    let imageUrl = $('#productImageUrl').value.trim();

    if (productImageFile) {
        const formData = new FormData();
        formData.append('file', productImageFile);
        try {
            const uploadResponse = await fetch(`${API.uploads}/products`, {
                method: 'POST',
                body: formData
            });
            if (!uploadResponse.ok) {
                throw new Error('Upload failed');
            }
            const uploadResult = await uploadResponse.json();
            imageUrl = uploadResult.url;
        } catch (err) {
            console.error('Upload error:', err);
            toast('อัปโหลดรูปภาพไม่สำเร็จ');
            return;
        }
    }

    const payload = {
        code: $('#productCode').value.trim(),
        name: $('#productName').value.trim(),
        price: parseFloat($('#productPrice').value || 0),
        stock: parseInt($('#productStock').value || 0),
        category: $('#productCategory').value,
        imageUrl: imageUrl
    };
    try {
        const r = await fetch(id ? `${API.products}/${id}` : API.products, {
            method: id ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!r.ok) throw new Error('Save product failed');
        closeModal('productModal');
        await loadProducts();
        clearProductImageSelection();
    } catch (err) { toast('บันทึกเมนูไม่สำเร็จ'); }
}

window.deleteProduct = async (id) => {
    if (!confirm('ต้องการลบเมนูนี้ใช่หรือไม่')) return;
    try {
        const r = await fetch(`${API.products}/${id}`, { method: 'DELETE' });
        if (r.ok) {
            toast('ลบเมนูสำเร็จ');
            await loadProducts();
        } else {
            const errText = await r.text();
            toast(`ลบเมนูไม่สำเร็จ: ${errText}\n\nหมายเหตุ: อาจเป็นเพราะสินค้าถูกใช้ในรายการขายแล้ว`);
        }
    } catch (err) { toast('เกิดข้อผิดพลาดในการลบเมนู'); }
}
