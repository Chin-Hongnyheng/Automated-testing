import { test, expect } from "@playwright/test";
import { allure } from "allure-playwright";

test("create product", async ({ request }) => {
  allure.epic("Catalog");
  allure.feature("Products");
  allure.severity("critical");

  const res = await test.step("POST /products", async () => {
    return request.post("/products", { data: { name: "Pen" } });
  });

  await allure.attachment("response", await res.text(), "application/json");

  expect(res.status()).toBe(201);
});
