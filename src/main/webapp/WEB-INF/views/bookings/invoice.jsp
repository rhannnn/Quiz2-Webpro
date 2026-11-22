<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" uri="https://prestige.local/functions" %>
<!DOCTYPE html>
<html lang="id" class="dark">
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
    <title>${p:h(booking.invoiceNo)} | Surabaya Prestige Motors</title>
    <style>
        @media print {
            body { background: #fff !important; color: #000 !important; }
            .no-print { display: none !important; }
            #paper { box-shadow: none !important; margin: 0 !important; max-width: none !important; }
        }
    </style>
</head>
<body class="min-h-screen px-4 py-10">
    <div class="no-print mx-auto mb-6 flex max-w-3xl flex-wrap items-center justify-between gap-3">
        <a href="${base}/my-bookings" class="caps text-muted hover:text-gold">&larr; Back to bookings</a>
        <button type="button" onclick="window.print()" class="btn-gold !px-4 !py-2 text-xs">Print / Save PDF</button>
    </div>
    <div id="paper" class="mx-auto max-w-3xl bg-white p-10 text-neutral-900 shadow-2xl">
        <div class="flex items-start justify-between border-b-2 border-neutral-900 pb-6">
            <div>
                <div class="font-display text-2xl font-extrabold uppercase">Surabaya Prestige Motors</div>
                <div class="mt-1 font-mono text-xs uppercase tracking-widest text-neutral-600">Motorsport &amp; Luxury Rental</div>
                <div class="mt-3 text-sm text-neutral-600">Jl. Mayjen Sungkono No. 188, Surabaya Barat, Jawa Timur 60225</div>
            </div>
            <div class="text-right">
                <div class="font-mono text-xs uppercase tracking-widest text-neutral-600">Invoice</div>
                <div class="font-mono text-xl font-bold">${p:h(booking.invoiceNo)}</div>
                <div class="mt-2 inline-block rounded border border-neutral-900 px-2 py-1 font-mono text-xs uppercase">${p:h(booking.status)}</div>
            </div>
        </div>

        <div class="mt-8 grid grid-cols-2 gap-6 text-sm">
            <div>
                <div class="font-mono text-xs uppercase tracking-widest text-neutral-500">Billed To</div>
                <div class="mt-1 font-semibold">${p:h(booking.userName)}</div>
                <div class="text-neutral-600">${p:h(booking.userEmail)}</div>
            </div>
            <div>
                <div class="font-mono text-xs uppercase tracking-widest text-neutral-500">Rental Period</div>
                <div class="mt-1 font-semibold">${p:date(booking.startDate)} - ${p:date(booking.endDate)}</div>
                <div class="text-neutral-600">${booking.days} day(s)</div>
            </div>
        </div>

        <table class="mt-8 w-full text-left text-sm">
            <thead>
                <tr class="border-b border-neutral-300 font-mono text-xs uppercase tracking-widest text-neutral-500">
                    <th class="py-2">Vehicle</th>
                    <th class="py-2 text-right">Rate / Day</th>
                    <th class="py-2 text-right">Days</th>
                    <th class="py-2 text-right">Amount</th>
                </tr>
            </thead>
            <tbody>
                <tr class="border-b border-neutral-200">
                    <td class="py-3 font-semibold">${p:h(booking.carBrand)} ${p:h(booking.carName)}</td>
                    <td class="py-3 text-right font-mono">IDR ${p:idr(booking.carPricePerDay)}</td>
                    <td class="py-3 text-right font-mono">${booking.days}</td>
                    <td class="py-3 text-right font-mono">IDR ${p:idr(booking.totalPrice)}</td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3" class="pt-4 text-right font-mono text-xs uppercase tracking-widest text-neutral-500">Total</td>
                    <td class="pt-4 text-right font-mono text-lg font-bold">IDR ${p:idr(booking.totalPrice)}</td>
                </tr>
            </tfoot>
        </table>

        <p class="mt-10 text-xs text-neutral-500">Total dihitung dari tarif harian dikalikan jumlah hari sewa. Dokumen ini dibuat otomatis dan sah tanpa tanda tangan.</p>
    </div>
</body>
</html>
