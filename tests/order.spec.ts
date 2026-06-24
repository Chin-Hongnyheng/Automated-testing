import { test, expect } from "@playwright/test";

const seedOrder = {
  product: "Keyboard",
  quantity: 2,
};

test.describe("Orders", () => {
  let orderId: number;

  test.beforeEach(async ({ request }) => {
    const r = await request.post("/orders", { data: seedOrder });
    orderId = (await r.json()).id;
  });

  test.afterEach(async ({ request }) => {
    await request.delete(`/orders/${orderId}`);
  });

  test("order can be fetched", async ({ request }) => {
    const res = await request.get(`/orders/${orderId}`);
    expect(res.ok()).toBeTruthy();
  });
});
