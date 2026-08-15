/**
 * Converts a raw caught error into a user-friendly message.
 * - Network/fetch errors → generic "backend unavailable" message
 * - Meaningful backend messages → shown as-is
 * - Anything else → generic fallback
 */
export function getFriendlyError(err) {
  const raw = (err?.message || '').trim();

  // Network-level failure: backend unreachable
  if (
    raw.toLowerCase().includes('failed to fetch') ||
    raw.toLowerCase().includes('networkerror') ||
    raw.toLowerCase().includes('err_connection') ||
    raw.toLowerCase().includes('load failed') ||       // Safari
    raw.toLowerCase().includes('network request failed')
  ) {
    return null; // caller will use the default connection message
  }

  // Meaningful backend validation message (e.g. "Email already in use")
  if (raw.length > 0) return raw;

  return null; // fall through to default
}

export const CONNECTION_ERROR =
  'Unable to connect to the server. Please make sure the backend server is running and try again.';
