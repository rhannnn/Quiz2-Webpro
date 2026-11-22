<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="Fleet | Surabaya Prestige Motors">
<div class="mx-auto max-w-[1440px] px-5 py-12 lg:px-12"
     x-data="{
        open: false,
        car: {},
        start: '',
        end: '',
        pick(c) { this.car = c; this.open = true; },
        get days() {
            if (!this.start || !this.end) return 0;
            const d = (new Date(this.end) - new Date(this.start)) / 86400000 + 1;
            return d > 0 ? d : 0;
        },
        get total() { return this.days * (this.car.price || 0); },
        idr(n) { return 'IDR ' + new Intl.NumberFormat('id-ID').format(n); }
     }">
    <div class="caps text-gold">Fleet Registry</div>
    <h1 class="mt-2 font-display text-5xl font-extrabold uppercase">The Fleet</h1>
    <div class="mt-6"><t:flash/></div>

    <form method="get" action="${base}/fleet" class="card mt-8 grid gap-4 p-5 md:grid-cols-4">
        <div class="md:col-span-2">
            <label class="label">Search</label>
            <input type="text" name="search" value="${p:h(filterSearch)}" placeholder="Nama atau merek" class="field">
        </div>
        <div>
            <label class="label">Category</label>
            <select name="category" class="field">
                <option value="">All</option>
                <c:forEach var="category" items="${categories}">
                    <option value="${p:h(category)}" ${filterCategory == category ? 'selected' : ''}>${p:h(category)}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="label">Sort</label>
            <select name="sort" class="field">
                <option value="">Newest</option>
                <option value="price_asc" ${filterSort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                <option value="price_desc" ${filterSort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
            </select>
        </div>
        <div class="md:col-span-4">
            <button class="btn-gold">Filter</button>
            <a href="${base}/fleet" class="btn-dark ml-2">Reset</a>
        </div>
    </form>

    <div class="mt-10 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <c:forEach var="car" items="${cars.items}">
            <t:carCard car="${car}" modal="${true}"/>
        </c:forEach>
        <c:if test="${empty cars.items}">
            <p class="text-muted">Kendaraan tidak ditemukan.</p>
        </c:if>
    </div>
    <div class="mt-8"><t:pagination pager="${cars}" path="/fleet" query="${baseQuery}"/></div>

    <div x-cloak x-show="open" x-transition.opacity class="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4" @keydown.escape.window="open = false">
        <div @click.outside="open = false" class="card max-h-[92vh] w-full max-w-lg overflow-y-auto bg-panel p-6">
            <div class="flex items-start justify-between">
                <div>
                    <div class="caps text-gold">VIP Reservation Console</div>
                    <h3 class="mt-1 font-display text-2xl font-bold uppercase" x-text="car.name"></h3>
                    <div class="font-mono text-sm text-champagne" x-text="idr(car.price || 0) + ' / day'"></div>
                </div>
                <button type="button" @click="open = false" class="text-2xl text-muted hover:text-gold">&times;</button>
            </div>
            <form method="post" :action="'${base}/fleet/' + car.id + '/book'" class="mt-6 space-y-5">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="redirect_to" value="list">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="label">Commence</label>
                        <input type="date" name="start_date" x-model="start" min="${p:today()}" class="field" required>
                    </div>
                    <div>
                        <label class="label">Conclude</label>
                        <input type="date" name="end_date" x-model="end" :min="start" class="field" required>
                    </div>
                </div>
                <div class="rounded border border-gold/20 bg-void p-4">
                    <div class="flex justify-between font-mono text-sm text-muted">
                        <span>Duration</span><span x-text="days + ' day(s)'"></span>
                    </div>
                    <div class="mt-2 flex items-end justify-between">
                        <span class="caps text-muted">Net Charter Total</span>
                        <span class="font-mono text-xl font-bold text-gold" x-text="idr(total)"></span>
                    </div>
                </div>
                <button class="btn-gold w-full" :disabled="days === 0">Confirm Booking</button>
            </form>
        </div>
    </div>
</div>
</t:layout>
