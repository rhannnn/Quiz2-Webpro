<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="${car.name} | Surabaya Prestige Motors">
<div class="mx-auto grid max-w-[1440px] gap-10 px-5 py-12 lg:grid-cols-2 lg:px-12">
    <div class="card overflow-hidden">
        <img onerror="this.onerror=null;this.src='${base}/assets/placeholder-car.svg'" src="${base}${car.imagePath}" alt="${p:h(car.name)}" class="w-full object-cover">
    </div>
    <div>
        <t:flash/>
        <div class="flex items-center justify-between gap-4">
            <div class="caps text-bronze">${p:h(car.brand)} // ${p:h(car.category)}</div>
            <c:if test="${not empty authUser}">
                <form method="post" action="${base}/favorites/${car.id}/toggle">
                    <input type="hidden" name="_csrf" value="${csrf}">
                    <input type="hidden" name="back" value="${p:h(currentUrl)}">
                    <button class="${car.favorite ? 'btn-gold' : 'btn-dark'} !px-4 !py-2 text-xs">${car.favorite ? '&#9829; Favorited' : '&#9825; Add to Favorites'}</button>
                </form>
            </c:if>
        </div>
        <h1 class="mt-2 font-display text-5xl font-extrabold uppercase leading-tight">${p:h(car.name)}</h1>
        <div class="mt-6 overflow-hidden rounded border border-white/5">
            <div class="flex justify-between bg-carbon/30 px-4 py-3 font-mono text-sm"><span class="text-muted">POWER</span><span class="text-gold">${car.hp} BHP</span></div>
            <div class="flex justify-between px-4 py-3 font-mono text-sm"><span class="text-muted">TOP SPEED</span><span class="text-gold">${car.topSpeed} KM/H</span></div>
            <div class="flex justify-between bg-carbon/30 px-4 py-3 font-mono text-sm"><span class="text-muted">STATUS</span><span class="text-gold uppercase">${p:h(car.status)}</span></div>
            <div class="flex justify-between px-4 py-3 font-mono text-sm"><span class="text-muted">RATE DAILY</span><span class="text-gold">IDR ${p:idr(car.pricePerDay)}</span></div>
        </div>

        <c:choose>
            <c:when test="${car.available}">
                <c:choose>
                    <c:when test="${not empty authUser}">
                        <form method="post" action="${base}/fleet/${car.id}/book" class="card mt-8 space-y-4 p-6"
                              x-data="{ start: '', end: '', price: ${car.priceInt},
                                get days() { if (!this.start || !this.end) return 0; const d = (new Date(this.end) - new Date(this.start)) / 86400000 + 1; return d > 0 ? d : 0; } }">
                            <input type="hidden" name="_csrf" value="${csrf}">
                            <div class="caps text-gold">Reserve This Vehicle</div>
                            <div class="grid grid-cols-2 gap-4">
                                <div>
                                    <label class="label">Start Date</label>
                                    <input type="date" name="start_date" x-model="start" min="${p:today()}" class="field" required>
                                </div>
                                <div>
                                    <label class="label">End Date</label>
                                    <input type="date" name="end_date" x-model="end" :min="start" class="field" required>
                                </div>
                            </div>
                            <div class="font-mono text-sm text-muted">
                                Total: <span class="text-lg font-bold text-gold" x-text="'IDR ' + new Intl.NumberFormat('id-ID').format(days * price)"></span>
                            </div>
                            <button class="btn-gold w-full" :disabled="days === 0">Book Now</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${base}/login" class="btn-gold mt-8">Login untuk Booking</a>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <div class="mt-8 rounded border border-red-400/40 bg-red-500/10 px-4 py-3 font-mono text-sm text-red-300">Kendaraan sedang disewa.</div>
            </c:otherwise>
        </c:choose>
        <a href="${base}/fleet" class="caps mt-8 inline-block text-muted hover:text-gold">&larr; Back to fleet</a>
    </div>
</div>
</t:layout>
