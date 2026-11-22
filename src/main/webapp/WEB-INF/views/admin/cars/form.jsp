<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="editing" value="${not empty car}"/>
<c:set var="actionUrl" value="${base}/admin/cars${editing ? '/' : ''}${editing ? car.id : ''}"/>
<t:adminLayout title="${editing ? 'Edit Vehicle' : 'Add Vehicle'} | SPM Command">
<div class="caps text-gold">Fleet Master Inventory Registry</div>
<h1 class="mt-2 font-display text-4xl font-extrabold uppercase">${editing ? 'Edit ' : 'Add Exotic Vehicle'}${editing ? p:h(car.name) : ''}</h1>

<form method="post" action="${actionUrl}?_csrf=${csrf}" enctype="multipart/form-data" class="card mt-8 max-w-4xl p-6">
    <input type="hidden" name="_csrf" value="${csrf}">
    <div class="grid gap-5 md:grid-cols-2">
        <div>
            <label class="label">Nama Kendaraan</label>
            <input type="text" name="name" value="${p:h(not empty old ? old.name : car.name)}" class="field" required>
        </div>
        <div>
            <label class="label">Brand</label>
            <input type="text" name="brand" value="${p:h(not empty old ? old.brand : car.brand)}" class="field" required>
        </div>
        <div>
            <c:set var="selectedCategory" value="${not empty old ? old.category : car.category}"/>
            <label class="label">Category</label>
            <select name="category" class="field">
                <c:forEach var="category" items="${categories}">
                    <option value="${p:h(category)}" ${selectedCategory == category ? 'selected' : ''}>${p:h(category)}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <c:set var="selectedStatus" value="${not empty old ? old.status : (editing ? car.status : 'available')}"/>
            <label class="label">Status</label>
            <select name="status" class="field">
                <c:forEach var="status" items="${statuses}">
                    <option value="${status}" ${selectedStatus == status ? 'selected' : ''}>${p:h(status)}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="label">Engine Power (BHP)</label>
            <input type="number" min="1" name="hp" value="${p:h(not empty old ? old.hp : (editing ? car.hp : ''))}" class="field" required>
        </div>
        <div>
            <label class="label">Top Speed (KM/H)</label>
            <input type="number" min="1" name="top_speed" value="${p:h(not empty old ? old.top_speed : (editing ? car.topSpeed : ''))}" class="field" required>
        </div>
        <div class="md:col-span-2">
            <label class="label">Daily Tariff (IDR)</label>
            <input type="number" min="0" step="1" name="price_per_day" value="${p:h(not empty old ? old.price_per_day : (editing ? car.priceInt : ''))}" class="field" required>
        </div>
        <div class="md:col-span-2" x-data="{ preview: '<c:if test="${editing and not empty car.image}">${base}${car.imagePath}</c:if>' }">
            <label class="label">Vehicle Image (JPG/PNG/WEBP, max 2MB)</label>
            <input type="file" name="image" accept="image/jpeg,image/png,image/webp" class="field"
                   @change="preview = $event.target.files[0] ? URL.createObjectURL($event.target.files[0]) : preview"
                   ${editing ? '' : 'required'}>
            <img x-show="preview" :src="preview" class="mt-4 max-h-56 rounded border border-gold/20" alt="">
        </div>
    </div>
    <div class="mt-8 flex gap-3">
        <a href="${base}/admin/cars" class="btn-dark">Cancel</a>
        <button class="btn-gold">Save Vehicle</button>
    </div>
</form>
</t:adminLayout>
