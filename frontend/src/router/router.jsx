import { createBrowserRouter } from "react-router";

import AppLayout from "../components/AppLayout";
import RootLayout from "../components/RootLayout";
import ProtectedRouter, { protectedLoader } from "./protectedRouter";

import Login from "../pages/Login";
import Signup from "../pages/Signup";
import Skills from "../pages/Skills";
import NotFound from "../pages/NotFound";
import ErrorPage from "../pages/ErrorPage";

const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
        // Public routes
        {
            path: "/login",
            Component: Login,
            ErrorBoundary: ErrorPage,
            handle: {
                title: "SkillSwap | Login",
            },
        },

        {
            path: "/signup",
            Component: Signup,
            ErrorBoundary: ErrorPage,
            handle: {
                title: "SkillSwap | Sign Up",
            },
        },

        {
            path: "/signup/skills",
            Component: Skills,
            ErrorBoundary: ErrorPage,
            handle: {
                title: "SkillSwap | Skills",
            },
        },

        // Protected routes
        {
            element: <ProtectedRouter />,
            loader: protectedLoader,
            ErrorBoundary: ErrorPage,
            children: [
            {
                element: <AppLayout />,
                children: [
                {
                    path: "/profile",
                    lazy: async () => {
                        const module = await import("../pages/Profile");
                        return { Component: module.default, };
                    },
                    handle: {
                        title: "SkillSwap | Profile",
                    },
                },

                {
                    path: "/matches",
                    lazy: async () => {
                        await new Promise((resolve) => setTimeout(resolve, 9000));
                        const module = await import("../pages/Matches");
                        return { Component: module.default, };
                    },
                    handle: {
                        title: "SkillSwap | Matches",
                    },
                },

                {
                    path: "/requests",
                    lazy: async () => {
                        const module = await import("../pages/Requests");
                        return { Component: module.default, };
                    },
                    handle: {
                        title: "SkillSwap | Requests",
                    },
                },

                {
                    path: "/chat",
                    lazy: async () => {
                        const module = await import("../pages/Chat");
                        return { Component: module.default, };
                    },
                    handle: {
                        title: "SkillSwap | Chat",
                    },
                },
                ],
            },
            ],
        },

        // All undefined paths
        {
            path: "*",
            Component: NotFound,
            handle: {
                title: "SkillSwap | Page Not Found",
            },
        },
    ],
  },
]);

export default router;