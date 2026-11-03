import { createBrowserRouter } from 'react-router';

import AppLayout from '../components/AppLayout';
import AuthLayout from '../components/AuthLayout';
import RootLayout from '../components/RootLayout';
import Spinner from '../components/Spinner';
import {
	chatLoader,
	friendsLoader,
	homeLoader,
	protectedLoader,
} from './loaders';

import Login from '../pages/Login';
import Signup from '../pages/Signup';
import NotFound from '../pages/NotFound';
import ErrorPage from '../pages/ErrorPage';

const router = createBrowserRouter([
	{
		element: <RootLayout />,
		children: [
			// Home route
			{
				path: '/',
				loader: homeLoader, // load authentication, if authenticated redirects to /matches, otherwise to /login
				ErrorBoundary: ErrorPage,
			},
			// Public routes. AuthLayout stays on screen while you switch between them
			{
				element: <AuthLayout />,
				children: [
					{
						path: '/login',
						Component: Login,
						ErrorBoundary: ErrorPage,
						handle: {
							title: 'SkillSwap | Login',
						},
					},

					{
						path: '/signup',
						Component: Signup,
						ErrorBoundary: ErrorPage,
						handle: {
							title: 'SkillSwap | Sign Up',
						},
					},
				],
			},

			// Protected routes
			{
				id: 'protected', // lets the Header read the logged-in user with useRouteLoaderData('protected')
				loader: protectedLoader, // loads authentications, if not authenticated redirects to /login
				ErrorBoundary: ErrorPage,
				HydrateFallback: Spinner, // shown while the login check runs on the first page load
				children: [
					{
						element: <AppLayout />,
						children: [
							{
								path: '/profile',
								lazy: async () => {
									const module =
										await import('../pages/Profile');
									return { Component: module.default };
								},
								handle: {
									title: 'SkillSwap | Profile',
								},
							},

							{
								path: '/matches',
								lazy: async () => {
									const module =
										await import('../pages/Matches');
									return { Component: module.default };
								},
								handle: {
									title: 'SkillSwap | Matches',
								},
							},

							{
								path: '/friends',
								loader: friendsLoader, // loads my connections before the page renders
								lazy: async () => {
									const module =
										await import('../pages/Friends');
									return { Component: module.default };
								},
								ErrorBoundary: ErrorPage,
								handle: {
									title: 'SkillSwap | Friends',
								},
							},

							{
								path: '/chat',
								loader: chatLoader,
								lazy: async () => {
									const module =
										await import('../pages/Chat');
									return {
										Component: module.default,
									};
								},
								handle: {
									title: 'SkillSwap | Chat',
								},
							},
						],
					},
				],
			},

			// All undefined paths
			{
				path: '*',
				Component: NotFound,
				handle: {
					title: 'SkillSwap | Page Not Found',
				},
			},
		],
	},
]);

export default router;
