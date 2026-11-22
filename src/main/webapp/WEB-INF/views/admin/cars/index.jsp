<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:adminLayout title="Fleet Inventory | SPM Command">
<div class="flex flex-wrap items-center justify-between gap-4">
    <div>
        <div class="caps text-gold">Fleet Master Inventory</div>
        <h1 class="mt-2 font-display text-4xl font-extrabold uppercase">Fleet Inventory</h1>
    </div>
    <a href="${base}/admin/cars/new" class="btn-gold">+ Add Vehicle</a>
</div>

<div class="card mt-8 overflow-x-auto">
    <table class="w-full min-w-[800px] text-left font-mono text-sm">
        <thead class="caps border-b border-gold/20 text-muted">
            <tr>
                <th class="p-4">Image</th>
                <th class="p-4">Vehicle</th>
                <th class="p-4">Category</th>
                <th class="p-4">Rate / Day</th>
                <th class="p-4">Status</th>
                <th class="p-4 text-right">Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="car" items="${cars.items}">
                <tr class="border-b border-white/5 odd:bg-carbon/30">
                    <td class="p-4"><img onerror="this.onerror=null;this.src='${base}/assets/placeholder-car.svg'" src="${base}${car.imagePath}" class="h-12 w-20 rounded object-cover" alt=""></td>
                    <td class="p-4 font-body text-base"><a href="${base}/admin/cars/${car.id}" class="hover:text-gold">${p:h(car.brand)} ${p:h(car.name)}</a></td>
                    <td class="p-4">${p:h(car.category)}</td>
                    <td class="p-4 text-gold">IDR ${p:idr(car.pricePerDay)}</td>
                    <td class="p-4 uppercase">${p:h(car.status)}</td>
                    <td class="p-4">
                        <div class="flex justify-end gap-2">
                            <a href="${base}/admin/cars/${car.id}/edit" class="btn-dark !px-3 !py-1 text-xs">Edit</a>
                            <form method="post" action="${base}/admin/cars/${car.id}/delete" onsubmit="return confirm('Hapus kendaraan ini beserta gambarnya?')">
                                <input type="hidden" name="_csrf" value="${csrf}">
                                <button class="btn-dark !px-3 !py-1 text-xs !text-red-300">Delete</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty cars.items}">
                <tr><td colspan="6" class="p-6 text-muted">Belum ada kendaraan.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>
<div class="mt-6"><t:pagination pager="${cars}" path="/admin/cars"/></div>
</t:adminLayout>
