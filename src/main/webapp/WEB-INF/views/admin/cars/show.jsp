<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:adminLayout title="${car.name} | SPM Command">
<div class="caps text-gold">${p:h(car.brand)} // ${p:h(car.category)}</div>
<h1 class="mt-2 font-display text-4xl font-extrabold uppercase">${p:h(car.name)}</h1>
<div class="card mt-8 max-w-3xl overflow-hidden">
    <img onerror="this.onerror=null;this.src='${base}/assets/placeholder-car.svg'" src="${base}${car.imagePath}" alt="${p:h(car.name)}" class="w-full">
    <div class="grid grid-cols-2 gap-4 p-6 font-mono text-sm">
        <div><span class="text-muted">BHP</span> <span class="text-gold">${car.hp}</span></div>
        <div><span class="text-muted">V-MAX</span> <span class="text-gold">${car.topSpeed} KM/H</span></div>
        <div><span class="text-muted">STATUS</span> <span class="uppercase text-gold">${p:h(car.status)}</span></div>
        <div><span class="text-muted">RATE</span> <span class="text-gold">IDR ${p:idr(car.pricePerDay)}</span></div>
    </div>
</div>
<a href="${base}/admin/cars" class="btn-dark mt-6">Back</a>
</t:adminLayout>
