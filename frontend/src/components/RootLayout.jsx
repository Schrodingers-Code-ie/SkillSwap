import { Outlet, useMatches } from "react-router";
import { useEffect } from "react";

export default function RootLayout() {
    const matches = useMatches();

    const title = matches
        .map((match) => match.handle?.title).filter(Boolean).pop();

    useEffect(() => {
        document.title = title || "SkillSwap";
    }, [title]);

    return <Outlet />;
}