import { Client } from '@stomp/stompjs';
import { useCallback, useEffect, useRef, useState } from 'react';
import { getCsrfToken } from '../../api/client';
import { SocketContext } from './socket-context';

// One STOMP connection per tab for the logged-in part of the app (it lives in AppLayout).
// Opens on mount (after login), closes on unmount (logout), and reconnects by itself.
// Components use useSocket(): { connected, subscribe(destination, callback) }.
// subscribe returns an unsubscribe function, so return it from useEffect.
export function SocketProvider({ children }) {
	const clientRef = useRef(null);
	// everything components asked to listen to, so it can be subscribed again after a reconnect
	const wantedRef = useRef(new Map());
	const [connected, setConnected] = useState(false);

	useEffect(() => {
		const scheme = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
		const client = new Client({
			brokerURL: `${scheme}//${window.location.host}/ws`,
			reconnectDelay: 3000,
			heartbeatIncoming: 10000,
			heartbeatOutgoing: 10000,
			beforeConnect: () => {
				client.connectHeaders = {
					'X-XSRF-TOKEN': getCsrfToken() ?? '',
				};
			},
			onConnect: () => {
				console.info('[socket] connected');
				setConnected(true);
				for (const w of wantedRef.current.values()) {
					w.stompSub = client.subscribe(w.destination, w.handler);
				}
			},
			onWebSocketClose: () => {
				console.info('[socket] disconnected');
				setConnected(false);
			},
			onStompError: (frame) => {
				console.error('[socket] error:', frame.headers.message);
			},
		});
		clientRef.current = client;
		client.activate();
		return () => {
			client.deactivate();
		};
	}, []);

	const subscribe = useCallback((destination, callback) => {
		const key = Symbol(destination);
		const w = {
			destination,
			handler: (message) => callback(JSON.parse(message.body)),
			stompSub: null,
		};
		wantedRef.current.set(key, w);
		if (clientRef.current?.connected) {
			w.stompSub = clientRef.current.subscribe(destination, w.handler);
		}
		return () => {
			wantedRef.current.delete(key);
			if (clientRef.current?.connected) w.stompSub?.unsubscribe();
		};
	}, []);

	return (
		<SocketContext.Provider value={{ connected, subscribe }}>
			{children}
		</SocketContext.Provider>
	);
}
