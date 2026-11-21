import { createContext, useContext } from 'react';

// Shared WebSocket connection, see SocketContext.jsx
export const SocketContext = createContext(null);
// Returns { connected, subscribe } from the nearest SocketProvider
export const useSocket = () => useContext(SocketContext);
