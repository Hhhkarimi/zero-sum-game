import type { MetadataRoute } from "next";

export default function manifest(): MetadataRoute.Manifest {
  return {
    name: "نقطه صفر",
    short_name: "نقطه صفر",
    description: "بازی عددی جمع و علامت",
    start_url: "/",
    display: "standalone",
    background_color: "#f2efe7",
    theme_color: "#171714",
    icons: [
      {
        src: "/icon.svg",
        sizes: "any",
        type: "image/svg+xml",
      },
    ],
  };
}
