<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ attribute name="car" required="true" type="com.prestige.model.Car" %>
<%@ attribute name="modal" required="false" type="java.lang.Boolean" %>
<article class="card group overflow-hidden transition hover:shadow-[0_0_35px_-8px_rgba(212,175,55,0.35)]">
    <a href="${base}/fleet/${car.id}" class="relative block aspect-[16/10] overflow-hidden bg-carbon">
        <img onerror="this.onerror=null;this.src='${base}/assets/placeholder-car.svg'" src="${base}${car.imagePath}" alt="${p:h(car.name)}" class="h-full w-full object-cover transition duration-500 group-hover:scale-105">
        <span class="caps absolute left-3 top-3 rounded bg-void/80 px-2 py-1 text-gold">${p:h(car.category)}</span>
        <span class="caps absolute right-3 top-3 rounded px-2 py-1 ${car.available ? 'bg-gold text-void' : 'bg-red-500/80 text-white'}">${p:h(car.status)}</span>
    </a>
    <div class="p-5">
        <div class="flex items-center justify-between gap-3">
            <div class="caps text-bronze">${p:h(car.brand)}</div>
            <c:if test="${not empty authUser}">
                <form method="post" action="${base}/favorites/${car.id}/toggle">
                    <input type="hidden" name="_csrf" value="${csrf}">
                    <input type="hidden" name="back" value="${p:h(currentUrl)}">
                    <button type="submit" title="${car.favorite ? 'Hapus dari favorit' : 'Tambah ke favorit'}"
                            class="text-xl leading-none ${car.favorite ? 'text-gold' : 'text-muted hover:text-gold'}">${car.favorite ? '&#9829;' : '&#9825;'}</button>
                </form>
            </c:if>
        </div>
        <h3 class="mt-1 font-display text-2xl font-bold uppercase leading-tight">${p:h(car.name)}</h3>
        <div class="mt-4 grid grid-cols-2 gap-px overflow-hidden rounded border border-white/5 bg-white/5 text-center">
            <div class="bg-panel p-3">
                <div class="caps text-muted">Power</div>
                <div class="font-mono text-sm font-bold text-gold">${car.hp} BHP</div>
            </div>
            <div class="bg-panel p-3">
                <div class="caps text-muted">V-Max</div>
                <div class="font-mono text-sm font-bold text-gold">${car.topSpeed} KM/H</div>
            </div>
        </div>
        <div class="mt-5 flex items-end justify-between gap-3">
            <div>
                <div class="caps text-muted">Rate Daily</div>
                <div class="font-mono text-lg font-bold text-gold">IDR ${p:idr(car.pricePerDay)}</div>
            </div>
            <c:choose>
                <c:when test="${modal}">
                    <c:choose>
                        <c:when test="${not empty authUser}">
                            <button type="button"
                                    @click="pick({id: ${car.id}, name: ${p:js(car.name)}, price: ${car.priceInt}})"
                                    class="btn-gold !px-4 !py-2 text-xs ${car.available ? '' : 'pointer-events-none opacity-40'}">
                                Book Vehicle
                            </button>
                        </c:when>
                        <c:otherwise>
                            <a href="${base}/login" class="btn-gold !px-4 !py-2 text-xs">Login to Book</a>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <a href="${base}/fleet/${car.id}" class="btn-dark !px-4 !py-2 text-xs">Details</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</article>
