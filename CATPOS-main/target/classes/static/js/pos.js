// src/main/resources/static/js/pos.js

let products = [];
let cart = [];
let currentPaymentMethod = 'cash';
let selectedCustomer = null;
let selectedCustomerDetail = null;
let useLoyaltyCoupon = false;
let currentCartSummary = null;
let customerSearchTimer = null;
let latestCustomerSearchResults = [];

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
            initializePosPage();
        } else {
            redirectToLogin();
        }
    });
});

function initializePosPage() {
    loadSettings().then(() => {
        loadProducts().then(() => {
            bindPosActions();
            updateSearchPlaceholder('pos');
        });
    });
}

function bindPosActions() {
    $('#searchInput').addEventListener('input', e => handleGlobalSearch(e.target.value));
    $('#btnPay').addEventListener('click', handlePayment);
    $('#btnClear').addEventListener('click', clearCart);
    $('#confirmPayment').addEventListener('click', processPayment);
    $('#cashReceived').addEventListener('input', calculateChange);

    $$('.modal').forEach(m => m.addEventListener('click', e => {
        if (e.target === m) {
            if (m.id === 'paymentModal' || m.id === 'paymentMethodModal') {
                cancelPaymentFlow(m.id);
            } else {
                closeModal(m.id);
            }
        }
    }));
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') {
            $$('.modal.show').forEach(m => {
                if (m.id === 'paymentModal' || m.id === 'paymentMethodModal') {
                    cancelPaymentFlow(m.id);
                } else {
                    closeModal(m.id);
                }
            });
        }
    });

    const customerSearchInput = $('#customerSearch');
    if (customerSearchInput) {
        customerSearchInput.addEventListener('input', handleCustomerSearchInput);
        document.addEventListener('click', (evt) => {
            const results = $('#customerSearchResults');
            if (!results) return;
            if (!results.contains(evt.target) && evt.target !== customerSearchInput) {
                hideCustomerSearchResults();
            }
        });
    }
}

function cancelPaymentFlow(modalId) {
    clearSelectedCustomer();
    closeModal(modalId);
    if (modalId === 'paymentModal') {
        closeModal('paymentMethodModal');
    }
}
window.cancelPaymentFlow = cancelPaymentFlow; // Make it globally accessible for onclick

// --- Settings (re-fetch for each page if needed, or rely on common.js cache) ---
async function loadSettings() {
    try {
        const r = await fetch(API.settings);
        settingsCache = r.ok ? await r.json() : {};
    } catch (err) { console.error('Error loading settings:', err); }
}

// --- Products ---
async function loadProducts() {
    try {
        const r = await fetch(API.products);
        products = r.ok ? await r.json() : [];
        renderCategories();
        renderProductsGrid(products);
    } catch (err) { console.error('Error loading products:', err); }
}

function renderCategories() {
    const cats = ['all', ...new Set(products.map(p => p.category || 'others'))];
    const el = $('#categoryTabs');
    el.innerHTML = cats.map((c, i) => {
        const label = c === 'all' ? 'ทั้งหมด' : getCatName(c);
        return `<div class="category-tab ${i === 0 ? 'active' : ''}" data-category="${c}">${label}</div>`;
    }).join('');
    $$('.category-tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            $$('.category-tab').forEach(t => t.classList.remove('active'));
            e.currentTarget.classList.add('active');
            filterProducts($('#searchInput').value.trim().toLowerCase());
        });
    });
}

function renderProductsGrid(list) {
    const grid = $('#productsGrid');
    const sortedList = [...list].sort((a, b) => (a.name || '').localeCompare(b.name || '', 'th')); // Simple sort for POS
    grid.innerHTML = sortedList.length ? sortedList.map(p => {
        const thumbUrl = p.imageUrl || DEFAULT_PRODUCT_IMAGE;
        const thumb = `<img src="${esc(thumbUrl)}" alt="${esc(p.name)}" class="product-thumb-img" onerror="this.src='${DEFAULT_PRODUCT_IMAGE}';">`;
        // No best seller badge on POS for now, as it relies on report data
        return `
      <div class="product-card" data-id="${p.id}">
        <div class="product-thumb">${thumb}</div>
        <div class="product-name">${esc(p.name)}</div>
        <div class="product-price">฿${fmtBaht(p.price)}</div>
        <div class="product-stock">คงเหลือ: ${p.stock ?? 0}</div>
      </div>`;
    }).join('') : '<p style="text-align:center; grid-column:1/-1;">ไม่พบเมนู</p>';
    $$('.product-card').forEach(card => card.addEventListener('click', () => addToCart(card.dataset.id)));
}

function filterProducts(term) {
    const activeTab = $('.category-tab.active');
    const activeCategory = activeTab ? activeTab.dataset.category : 'all';
    let filtered = products;
    if (activeCategory !== 'all') {
        filtered = filtered.filter(p => (p.category || 'others') === activeCategory);
    }
    if (term) {
        filtered = filtered.filter(p => (p.name || '').toLowerCase().includes(term));
    }
    renderProductsGrid(filtered);
}

function handleGlobalSearch(rawTerm) {
    const term = (rawTerm || '').trim().toLowerCase();
    filterProducts(term);
}

function updateSearchPlaceholder(sectionId) {
    const input = $('#searchInput');
    const header = $('.header');
    if (input) {
        input.placeholder = '🔍 ค้นหาเมนู...';
    }
    if (header) {
        header.classList.remove('hidden'); // POS header is always visible
    }
}

// --- Cart ---
function addToCart(productId) {
    const product = products.find(p => p.id == productId);
    if (!product) return;
    if (product.stock <= 0) return toast('สินค้าหมด');

    const cartItem = cart.find(item => item.id == productId);
    if (cartItem) {
        if (cartItem.quantity < product.stock) {
            cartItem.quantity++;
        } else {
            return toast('สต็อกไม่พอ');
        }
    } else {
        cart.push({ ...product, quantity: 1 });
    }
    renderCart();
}

function renderCart() {
    const cartItemsDiv = $('#cartItems');
    if (cart.length === 0) {
        cartItemsDiv.innerHTML = `<div class="empty-cart"><div class="empty-cart-icon">🐱</div><p>ไม่มีเมนูในตะกร้า</p></div>`;
    } else {
        cartItemsDiv.innerHTML = cart.map(item => `
        <div class="cart-item">
          <div class="cart-item-info">
            <div class="cart-item-thumb"><img src="${esc(item.imageUrl || 'https://via.placeholder.com/40?text=No')}" alt="${esc(item.name)}" onerror="this.src='https://via.placeholder.com/40?text=No';"></div>
            <div>
              <div class="cart-item-name">${esc(item.name)}</div>
              <div class="cart-item-details">฿${fmtBaht(item.price)} x ${item.quantity}</div>
            </div>
          </div>
          <div class="quantity-controls">
            <button class="qty-btn" onclick="changeQuantity(${item.id}, -1)">-</button>
            <span>${item.quantity}</span>
            <button class="qty-btn" onclick="changeQuantity(${item.id}, 1)">+</button>
            <button class="qty-btn qty-btn-remove" onclick="removeFromCart(${item.id})">🗑️</button>
          </div>
        </div>`).join('');
    }
    $('#cartTotal').textContent = fmtBaht(cart.reduce((s, i) => s + i.price * i.quantity, 0));
}

function changeQuantity(productId, delta) {
    const item = cart.find(i => i.id == productId);
    if (!item) return;
    const p = products.find(x => x.id == productId);
    const newQty = item.quantity + delta;
    if (newQty <= 0) {
        removeFromCart(productId);
    } else if (p && newQty <= (p.stock ?? 0)) {
        item.quantity = newQty;
    } else {
        toast('สต็อกไม่พอ');
    }
    renderCart();
}

function removeFromCart(productId) {
    cart = cart.filter(i => i.id != productId);
    renderCart();
}

function clearCart() {
    cart = [];
    renderCart();
    currentCartSummary = null;
    updatePaymentSummaryDisplay();
}

// --- Payment ---
async function handlePayment() {
    if (cart.length === 0) return toast('กรุณาเลือกสินค้าก่อนชำระเงิน');

    clearSelectedCustomer();

    const reviewItemsDiv = $('#paymentReviewItems');
    if (cart.length > 0) {
        const items = cart.map((item, index) => {
            const imageUrl = esc(item.imageUrl || DEFAULT_PRODUCT_IMAGE);
            const name = esc(item.name);
            const qty = item.quantity;
            const price = fmtBaht(item.price);
            const total = fmtBaht(item.price * item.quantity);
            return `
                <div style="display:flex; align-items:center; gap:10px; margin-bottom:8px; background:white; border-radius:10px; padding:8px 10px;">
                    <div style="width:44px; height:44px; border-radius:8px; overflow:hidden; flex-shrink:0; background:#ffffff;">
                        <img src="${imageUrl}" alt="${name}" style="width:100%; height:100%; object-fit:cover;" onerror="this.src='${DEFAULT_PRODUCT_IMAGE}';">
                    </div>
                    <div style="flex:1;">
                        <div style="font-weight:600; color:#3e2723; font-size:0.95em;">${index + 1}. ${name}</div>
                        <div style="font-size:0.85em; color:#795548;">${qty} ชิ้น x ฿${price}</div>
                    </div>
                    <div style="font-weight:600; color:#ff6f00; font-size:0.95em;">฿${total}</div>
                </div>`;
        }).join('');

        reviewItemsDiv.innerHTML = `
            <div style="font-weight:600; margin-bottom:10px; color:#5d4037;">รายการสินค้าในตะกร้า</div>
            <div>${items}</div>
        `;
    } else {
        reviewItemsDiv.innerHTML = '<p style="text-align:center; color:#795548;">ไม่มีสินค้าในตะกร้า</p>';
    }

    try {
        const summary = await calculateCartSummary();
        if (summary?.stockAvailable === false) {
            return toast(summary.errorMessage || 'สินค้าในตะกร้ามีจำนวนไม่เพียงพอ');
        }
        updatePaymentSummaryDisplay(summary);
    } catch (error) {
        console.error('Cart calculation failed:', error);
        return toast('ไม่สามารถคำนวณยอดชำระได้');
    }

    showModal('paymentMethodModal');
}

async function selectPaymentMethod(method) {
    currentPaymentMethod = method;
    closeModal('paymentMethodModal');

    try {
        if (!currentCartSummary) {
            const summary = await calculateCartSummary();
            updatePaymentSummaryDisplay(summary);
        } else {
            updatePaymentSummaryDisplay(currentCartSummary);
        }
    } catch (error) {
        console.error('Failed to refresh cart summary:', error);
    }

    const total = currentCartSummary?.total ?? cart.reduce((s, i) => s + i.price * i.quantity, 0);

    if (method === 'cash') {
        $('#paymentTitle').textContent = '💰 ชำระเงินสด';
        $('#cashPayment').style.display = 'block';
        $('#qrPayment').style.display = 'none';
        $('#cashReceived').value = '';
        $('#changeAmount').value = '';
    } else {
        $('#paymentTitle').textContent = '📱 ชำระผ่าน QR';
        $('#cashPayment').style.display = 'none';
        $('#qrPayment').style.display = 'block';
        $('#qrAmount').textContent = fmtBaht(total);
    }

    const summaryForDisplay = currentCartSummary ? { ...currentCartSummary, total } : { total };
    updatePaymentSummaryDisplay(summaryForDisplay);
    showModal('paymentModal');
}
window.selectPaymentMethod = selectPaymentMethod; // Make it globally accessible for onclick

function calculateChange() {
    const totalField = $('#totalAmount');
    const total = totalField?.dataset?.rawValue ? parseFloat(totalField.dataset.rawValue) : parseFloat((totalField?.value || '').replace(/[^\\d.]/g, ''));
    const received = parseFloat($('#cashReceived').value);
    if (Number.isFinite(received) && Number.isFinite(total) && received >= total) {
        $('#changeAmount').value = `฿${fmtBaht(received - total)}`;
    } else {
        $('#changeAmount').value = '';
    }
}

async function processPayment() {
    if (cart.length === 0) return toast('ไม่มีสินค้าในตะกร้า');

    try {
        const summary = await calculateCartSummary();
        if (summary?.stockAvailable === false) {
            return toast(summary.errorMessage || 'สินค้าในตะกร้ามีจำนวนไม่เพียงพอ');
        }
        updatePaymentSummaryDisplay(summary);
    } catch (error) {
        console.error('Cart calculation failed:', error);
        return toast('ไม่สามารถคำนวณยอดชำระได้');
    }

    const payableTotal = currentCartSummary?.total ?? cart.reduce((s, i) => s + i.price * i.quantity, 0);
    if (currentPaymentMethod === 'cash') {
        const received = parseFloat($('#cashReceived').value || 0);
        if (!Number.isFinite(received) || received < payableTotal) {
            return toast('จำนวนเงินไม่เพียงพอ');
        }
    }

    const payload = {
        items: mapCartItemsForPayload(),
        paymentMethod: currentPaymentMethod,
        customerId: selectedCustomer?.id,
        useCoupon: useLoyaltyCoupon
    };

    if (currentPaymentMethod === 'cash') {
        payload.cashReceived = parseFloat($('#cashReceived').value || 0);
    }

    try {
        const response = await fetch(`${API.payment}/process`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) throw new Error(await response.text());
        const result = await response.json();

        if (!result.success) {
            return toast(result.errorMessage || result.message || 'ชำระเงินไม่สำเร็จ');
        }

        clearCart();
        await loadProducts(); // Reload products to update stock
        // await loadCustomers(); // Customers might be updated with loyalty points, but not critical for POS
        currentCartSummary = null;
        updatePaymentSummaryDisplay();
        clearSelectedCustomer();
        closeModal('paymentModal');
        closeModal('paymentMethodModal');

        if (result.receiptHtml) {
            $('#receiptContent').innerHTML = result.receiptHtml;
        } else {
            $('#receiptContent').innerHTML = '<p style="text-align:center;">ชำระเงินสำเร็จ</p>';
        }

        $('#cashReceived').value = '';
        $('#changeAmount').value = '';

        showModal('receiptModal');
    } catch (err) {
        console.error('Payment failed:', err);
        toast('ชำระเงินไม่สำเร็จ\n' + err.message);
    }
}

window.printReceipt = () => {
    const content = $('#receiptContent').innerHTML;
    const pwin = window.open('', 'print_content', 'width=380,height=500');
    pwin.document.open();
    pwin.document.write(`<html><head><title>ใบเสร็จ</title><style>body{font-family:monospace;font-size:13px;}</style></head><body>${content}</body></html>`);
    pwin.document.close();
    setTimeout(() => { pwin.print(); pwin.close(); }, 500);
};

window.emailReceipt = () => toast('ฟังก์ชันส่งอีเมลยังไม่พร้อมใช้งาน');

// --- Customer Search & Loyalty ---
function handleCustomerSearchInput(e) {
    const query = e.target.value.trim();
    clearTimeout(customerSearchTimer);

    if (query.length < 2) {
        latestCustomerSearchResults = [];
        hideCustomerSearchResults();
        return;
    }

    customerSearchTimer = setTimeout(async () => {
        try {
            const response = await fetch(`${API.loyalty}/search?query=${encodeURIComponent(query)}`, {
                cache: 'no-store'
            });
            if (!response.ok) throw new Error('Search failed');
            const results = await response.json();
            latestCustomerSearchResults = Array.isArray(results) ? results : [];
            renderCustomerSearchResults(latestCustomerSearchResults);
        } catch (error) {
            console.error('Customer search failed:', error);
            toast('ค้นหาลูกค้าไม่สำเร็จ');
        }
    }, 300);
}

function renderCustomerSearchResults(results) {
    const container = $('#customerSearchResults');
    if (!container) return;

    if (!results.length) {
        container.innerHTML = '<div class="search-result-item">ไม่พบลูกค้า</div>';
        container.style.display = 'block';
        return;
    }

    container.innerHTML = results.map((c, idx) => `
        <div class="search-result-item" data-index="${idx}">
            <div class="customer-info">
                <span><strong>${esc(c.customerName || '')}</strong></span>
                <span class="loyalty-badge">${(c.currentPoints ?? 0)} แต้ม</span>
            </div>
            <div style="font-size: 0.85em; color: #795548; margin-top: 6px;">
                แลกคูปองได้ ${(c.redeemableCoupons ?? 0)} ใบ | ต้องการอีก ${(c.pointsToNextCoupon ?? 0)} แต้ม
            </div>
        </div>
    `).join('');

    container.style.display = 'block';
    container.querySelectorAll('.search-result-item').forEach(el => {
        el.addEventListener('click', () => {
            const idx = Number(el.dataset.index);
            const customer = latestCustomerSearchResults[idx];
            if (customer) selectCustomer(customer);
        });
    });
}

function hideCustomerSearchResults() {
    const container = $('#customerSearchResults');
    if (container) {
        container.style.display = 'none';
    }
}

async function selectCustomer(customer) {
    selectedCustomer = {
        id: customer.customerId ?? customer.id,
        name: customer.customerName ?? customer.name,
        points: customer.currentPoints ?? customer.loyaltyPoints ?? 0
    };
    useLoyaltyCoupon = false;
    hideCustomerSearchResults();

    const input = $('#customerSearch');
    if (input) input.value = selectedCustomer.name;

    await fetchCustomerLoyaltyDetail(selectedCustomer.id);
    updateSelectedCustomerInfo();

    try {
        const summary = await calculateCartSummary();
        updatePaymentSummaryDisplay(summary);
    } catch (error) {
        console.error('Failed to refresh cart summary:', error);
    }
}

async function fetchCustomerLoyaltyDetail(customerId) {
    selectedCustomerDetail = null;
    if (!customerId) return;
    try {
        const response = await fetch(`${API.loyalty}/detail/${customerId}`, {
            cache: 'no-store'
        });
        if (response.ok) {
            selectedCustomerDetail = await response.json();
        }
    } catch (error) {
        console.error('Fetch loyalty detail failed:', error);
    }
}

function updateSelectedCustomerInfo() {
    const infoContainer = $('#selectedCustomerInfo');
    const detailsEl = $('#customerDetails');
    const loyaltyEl = $('#loyaltyInfo');
    if (!infoContainer || !detailsEl || !loyaltyEl) return;

    if (!selectedCustomer) {
        infoContainer.style.display = 'none';
        detailsEl.innerHTML = '';
        loyaltyEl.innerHTML = '';
        return;
    }

    infoContainer.style.display = 'block';
    detailsEl.innerHTML = `
        <div><strong>${esc(selectedCustomer.name)}</strong></div>
        <div style="color:#795548; font-size:0.9em;">แต้มสะสมปัจจุบัน: ${selectedCustomerDetail?.totalPoints ?? selectedCustomer.points ?? 0}</div>
    `;

    if (selectedCustomerDetail) {
        const totalPoints = selectedCustomerDetail.totalPoints ?? selectedCustomerDetail.currentPoints ?? 0;
        const availableCoupons = Math.floor(totalPoints / 100);
        const remainder = totalPoints % 100;
        const pointsNeeded = remainder === 0 ? 100 : 100 - remainder;
        const buttonLabel = useLoyaltyCoupon ? 'ยกเลิกการใช้คูปอง' : `ใช้คูปองส่วนลด (เหลือ ${availableCoupons} ใบ)`;
        const buttonClass = useLoyaltyCoupon ? 'btn btn-danger' : 'btn btn-success';

        loyaltyEl.innerHTML = `
            <div>แต้มสะสมทั้งหมด: <strong>${totalPoints}</strong></div>
            <div>คูปองที่แลกได้: <strong>${availableCoupons}</strong></div>
            <div>แต้มที่ต้องการสำหรับคูปองถัดไป: <strong>${pointsNeeded}</strong></div>
            ${availableCoupons > 0 ? `<button type="button" class="${buttonClass}" id="btnToggleCoupon" style="margin-top:12px;">${buttonLabel}</button>` : '<div style="margin-top:10px; color:#8d6e63;">แต้มยังไม่พอสำหรับคูปอง</div>'}
        `;

        if (availableCoupons > 0) {
            const btn = $('#btnToggleCoupon');
            if (btn) btn.addEventListener('click', toggleUseCoupon);
        }
    } else {
        loyaltyEl.innerHTML = '<div style="color:#8d6e63;">ไม่สามารถดึงข้อมูลแต้มสะสมได้</div>';
    }
}

async function toggleUseCoupon() {
    useLoyaltyCoupon = !useLoyaltyCoupon;
    updateSelectedCustomerInfo();
    try {
        const summary = await calculateCartSummary();
        updatePaymentSummaryDisplay(summary);
    } catch (error) {
        console.error('Failed to recalculate cart with coupon:', error);
    }
}

function clearSelectedCustomer() {
    selectedCustomer = null;
    selectedCustomerDetail = null;
    useLoyaltyCoupon = false;
    latestCustomerSearchResults = [];
    const input = $('#customerSearch');
    if (input) input.value = '';
    hideCustomerSearchResults();
    updateSelectedCustomerInfo();
    currentCartSummary = null;
    updatePaymentSummaryDisplay();
}
window.clearSelectedCustomer = clearSelectedCustomer; // Make it globally accessible for onclick

async function calculateCartSummary() {
    if (cart.length === 0) {
        currentCartSummary = {
            subtotal: 0,
            discount: 0,
            total: 0,
            stockAvailable: true,
            loyaltyPointsEarned: 0
        };
        return currentCartSummary;
    }

    const params = new URLSearchParams();
    if (selectedCustomer?.id) params.append('customerId', selectedCustomer.id);
    if (useLoyaltyCoupon) params.append('useCoupon', 'true');

    const url = `${API.cart}/calculate${params.toString() ? `?${params.toString()}` : ''}`;

    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(mapCartItemsForPayload())
    });

    if (!response.ok) {
        throw new Error(await response.text());
    }

    currentCartSummary = await response.json();
    return currentCartSummary;
}

function mapCartItemsForPayload() {
    return cart.map(item => ({
        productId: item.id,
        productName: item.name,
        imageUrl: item.imageUrl,
        price: Number(item.price || 0),
        quantity: Number(item.quantity || 0),
        subtotal: Number((item.price || 0) * (item.quantity || 0))
    }));
}

function updatePaymentSummaryDisplay(summary = currentCartSummary) {
    const totalValue = summary?.total ?? cart.reduce((s, i) => s + i.price * i.quantity, 0);
    $('#paymentModalTotal').textContent = fmtBaht(totalValue);

    const discountEl = $('#loyaltyDiscount');
    if (discountEl) {
        const discount = summary?.discount ?? 0;
        if (discount > 0) {
            discountEl.style.display = 'block';
            $('#discountAmount').textContent = fmtBaht(discount);
        } else {
            discountEl.style.display = 'none';
        }
    }

    const totalField = $('#totalAmount');
    if (totalField) {
        totalField.value = `฿${fmtBaht(totalValue)}`;
        totalField.dataset.rawValue = totalValue;
    }

    const qrAmount = $('#qrAmount');
    if (qrAmount) {
        qrAmount.textContent = fmtBaht(totalValue);
    }
}
