/**
 * @jest-environment jsdom
 */
import { screen } from "@testing-library/vue";
import NoteShowPage from "@/pages/NoteShowPage.vue";
import store from "../../src/store/index.js";
import { renderWithStoreAndMockRoute } from "../helpers";
import flushPromises from "flush-promises";
import _ from "lodash";
import makeMe from "../fixtures/makeMe";

beforeEach(() => {
  fetch.resetMocks();
});

describe("note show", () => {
  test("fetch API to be called SIXTH time after 6 seconds", async () => {
    const note = makeMe.aNote.please()
    const stubResponse = {
      notePosition: makeMe.aNotePosition.inCircle('a circle').please(),
      notes: [ note ]
    };
    fetch.mockResponse(JSON.stringify(stubResponse));
    renderWithStoreAndMockRoute(store, NoteShowPage, {
      propsData: { noteId: note.id },
    });
    await flushPromises();
    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(`/api/notes/${note.id}`, {});
    await screen.findByText("a circle");
  });
});
