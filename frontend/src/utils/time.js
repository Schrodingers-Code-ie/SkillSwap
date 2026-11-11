const DAY_MS = 86_400_000; // milliseconds in one day

// How many calendar days ago a date was: 0 = today, 1 = yesterday, ...
// Compares the start of each day, so 23:59 yesterday vs 00:01 today is 1 day, not 0
function daysAgo(date) {
	const startOfDay = (d) =>
		new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
	// Math.round, because on clock-change days a day is 23 or 25 hours long
	return Math.round((startOfDay(new Date()) - startOfDay(date)) / DAY_MS);
}

// Day divider: "Today", "Yesterday", "Monday", "3 Nov 2026"
export function dayLabel(iso) {
	const date = new Date(iso);
	const days = daysAgo(date);

	if (days === 0) return 'Today';
	if (days === 1) return 'Yesterday';
	if (days < 7) return date.toLocaleDateString('en-IE', { weekday: 'long' });
	return date.toLocaleDateString('en-IE', {
		day: 'numeric',
		month: 'short',
		year: 'numeric',
	});
}

// "14:05"
export function timeLabel(iso) {
	return new Date(iso).toLocaleTimeString('en-IE', {
		hour: '2-digit',
		minute: '2-digit',
	});
}

// "14:05" today, "Mon" in the last 7 days, "3 Nov" before that
export function previewTimeLabel(iso) {
	const date = new Date(iso);
	const days = daysAgo(date);

	if (days === 0) return timeLabel(iso);
	if (days < 7) return date.toLocaleDateString('en-IE', { weekday: 'short' });
	return date.toLocaleDateString('en-IE', { day: 'numeric', month: 'short' });
}
