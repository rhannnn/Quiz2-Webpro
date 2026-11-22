<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout title="My Bookings | Surabaya Prestige Motors">
<div class="mx-auto max-w-[1440px] px-5 py-12 lg:px-12">
    <div class="caps text-gold">VIP Manifest</div>
    <h1 class="mt-2 font-display text-5xl font-extrabold uppercase">My Bookings</h1>
    <div class="mt-6"><t:flash/></div>
    <div class="card mt-8 overflow-x-auto">
        <table class="w-full min-w-[800px] text-left font-mono text-sm">
            <thead class="caps border-b border-gold/20 text-muted">
                <tr>
                    <th class="p-4">Vehicle</th>
                    <th class="p-4">Period</th>
                    <th class="p-4">Total</th>
                    <th class="p-4">Status</th>
                    <th class="p-4"></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="booking" items="${bookings.items}">
                    <tr class="border-b border-white/5 odd:bg-carbon/30">
                        <td class="p-4 font-body text-base">${p:h(booking.carBrand)} ${p:h(booking.carName)}</td>
                        <td class="p-4">${p:date(booking.startDate)} - ${p:date(booking.endDate)}</td>
                        <td class="p-4 text-gold">IDR ${p:idr(booking.totalPrice)}</td>
                        <td class="p-4 uppercase">${p:h(booking.status)}</td>
                        <td class="p-4">
                            <div class="flex justify-end gap-2">
                                <a href="${base}/my-bookings/${booking.id}/invoice" class="btn-dark !px-3 !py-1 text-xs">Invoice</a>
                                <c:if test="${booking.pending}">
                                    <form method="post" action="${base}/my-bookings/${booking.id}/cancel">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <button class="btn-dark !px-3 !py-1 text-xs">Cancel</button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty bookings.items}">
                    <tr><td colspan="5" class="p-6 text-muted">Belum ada booking.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
    <div class="mt-6"><t:pagination pager="${bookings}" path="/my-bookings"/></div>
</div>
</t:layout>
