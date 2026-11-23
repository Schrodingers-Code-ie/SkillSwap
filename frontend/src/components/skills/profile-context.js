// The context ProfileProvider fills, and useProfile() to read it. In its own file
// so the provider file only exports a component (needed for fast refresh).
import { createContext, useContext } from 'react';

export const ProfileContext = createContext(null);
export const useProfile = () => useContext(ProfileContext);
