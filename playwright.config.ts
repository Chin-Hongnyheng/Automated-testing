import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  use: {
    baseURL: "http://localhost:3000",
    extraHTTPHeaders: {
      Accept: "application/json",
    },
    trace: "on-first-retry",
  },
  webServer: {
    command: "npm run start:dev",
    url: "http://localhost:3000",
    reuseExistingServer: !process.env.CI,
  },
  projects: [
    { name: "setup", testMatch: /auth\.setup\.ts/ },
    {
      name: "api tests",
      dependencies: ["setup"],
    },
  ],
  reporter: [
    ["line"],
    [
      "allure-playwright",
      {
        resultsDir: "allure-results",
        detail: true,
        environmentInfo: {
          API: "http://localhost:3000",
          framework: "NestJS",
          node: process.version,
        },
      },
    ],
  ],
});
