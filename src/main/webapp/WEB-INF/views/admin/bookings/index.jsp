<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:adminLayout title="VIP Manifest | SPM Command">
<div class="caps text-gold">Reservation Control</div>
<h1 class="mt-2 font-display text-4xl font-extrabold uppercase">VIP Manifest</h1>

<form method="get" action="${base}/admin/bookings" class="mt-6 flex items-end gap-3">
    <div>
        <label class="label">Filter Status</label>
        <select name="status" class="field">
            <option value="">All</option>
            <c:forEach var="status" items="${statuses}">
                <option value="${status}" ${filterStatus == status ? 'selected' : ''}>${p:h(status)}</option>
            </c:forEach>
        </select>
    </div>
    <button class="btn-gold">Filter</button>
</form>

<div class="card mt-6 overflow-x-auto">
    <table class="w-full min-w-[1000px] text-left font-mono text-sm">
        <thead class="caps border-b border-gold/20 text-muted">
            <tr>
                <th class="p-4">Customer</th>
                <th class="p-4">Vehicle</th>
                <th class="p-4">Period</th>
                <th class="p-4">Total</th>
                <th class="p-4">Status</th>
                <th class="p-4">Update</th>
                <th class="p-4"></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="booking" items="${bookings.items}">
                <tr class="border-b border-white/5 odd:bg-carbon/30">
                    <td class="p-4 font-body">${p:h(booking.userName)}<div class="text-xs text-muted">${p:h(booking.userEmail)}</div></td>
                    <td class="p-4">${p:h(booking.carName)}</td>
                    <td class="p-4">${p:date(booking.startDate)} - ${p:date(booking.endDate)}</td>
                    <td class="p-4 text-gold">IDR ${p:idr(booking.totalPrice)}</td>
                    <td class="p-4 uppercase">${p:h(booking.status)}</td>
                    <td class="p-4">
                        <form method="post" action="${base}/admin/bookings/${booking.id}/status" class="flex gap-2">
                            <input type="hidden" name="_csrf" value="${csrf}">
                            <input type="hidden" name="back" value="${p:h(currentUrl)}">
                            <select name="status" class="field !w-auto !py-1">
                                <c:forEach var="status" items="${statuses}">
                                    <option value="${status}" ${booking.status == status ? 'selected' : ''}>${p:h(status)}</option>
                                </c:forEach>
                            </select>
                            <button class="btn-gold !px-3 !py-1 text-xs">Save</button>
                        </form>
                    </td>
                    <td class="p-4"><a href="${base}/my-bookings/${booking.id}/invoice" class="btn-dark !px-3 !py-1 text-xs">Invoice</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty bookings.items}">
                <tr><td colspan="7" class="p-6 text-muted">Belum ada booking.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>
<div class="mt-6"><t:pagination pager="${bookings}" path="/admin/bookings" query="${baseQuery}"/></div>
</t:adminLayout>
