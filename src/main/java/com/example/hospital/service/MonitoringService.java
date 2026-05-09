package com.example.hospital.service;

import com.example.hospital.model.MetricsSnapshot;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Service
public class MonitoringService {

    private final LongAdder totalBookings = new LongAdder();
    private final LongAdder successfulBookings = new LongAdder();
    private final LongAdder failedBookings = new LongAdder();
    private final LongAdder successfulCancellations = new LongAdder();
    private final LongAdder failedCancellations = new LongAdder();
    private final AtomicLong totalBookingLatencyNanos = new AtomicLong();

    public void recordBooking(boolean success, long startedAtNanos) {
        totalBookings.increment();
        if (success) {
            successfulBookings.increment();
        } else {
            failedBookings.increment();
        }

        long latency = System.nanoTime() - startedAtNanos;
        totalBookingLatencyNanos.addAndGet(latency);
    }

    public void recordCancellation(boolean success, long startedAtNanos) {
        if (success) {
            successfulCancellations.increment();
        } else {
            failedCancellations.increment();
        }
    }

    public MetricsSnapshot snapshot() {
        long total = totalBookings.sum();
        long success = successfulBookings.sum();
        long failed = failedBookings.sum();
        double avgLatencyMillis = total == 0
                ? 0.0
                : totalBookingLatencyNanos.get() / 1_000_000.0 / total;

        return new MetricsSnapshot(
                total,
                success,
                failed,
                successfulCancellations.sum(),
                failedCancellations.sum(),
                avgLatencyMillis
        );
    }
}
