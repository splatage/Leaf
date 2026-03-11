package io.splatage.leaf.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FailedTargetLRU {

    private final int maxEntries;
    private final LinkedHashMap<Long, Long> expiries = new LinkedHashMap<>(16, 0.75F, true);

    public FailedTargetLRU(final int maxEntries) {
        this.maxEntries = Math.max(1, maxEntries);
    }

    public boolean isFailed(final long packedPos, final long now) {
        final Long expiry = this.expiries.get(packedPos);
        if (expiry == null) {
            return false;
        }
        if (expiry <= now) {
            this.expiries.remove(packedPos);
            return false;
        }
        return true;
    }

    public void noteFailed(final long packedPos, final long expiryTick) {
        if (expiryTick <= 0L) {
            this.expiries.remove(packedPos);
            return;
        }
        this.expiries.put(packedPos, expiryTick);
        this.trim();
    }

    public void clear() {
        this.expiries.clear();
    }

    private void trim() {
        while (this.expiries.size() > this.maxEntries) {
            final Iterator<Map.Entry<Long, Long>> iterator = this.expiries.entrySet().iterator();
            if (!iterator.hasNext()) {
                return;
            }
            iterator.next();
            iterator.remove();
        }
    }
}
