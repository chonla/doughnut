import ManagedApi from './ManagedApi';

const storedApi = (component, options={}) => {
  const managedApi = new ManagedApi(component, options);
  const store = component.$store

  function loadReviewPointViewedByUser(data) {
    if (!data) return;
    const { noteWithPosition, linkViewedbyUser } = data;
    if (noteWithPosition) {
      store.commit('loadNotes', [noteWithPosition.note]);
    }
    if (linkViewedbyUser) {
      loadReviewPointViewedByUser({
        noteWithPosition: linkViewedbyUser.sourceNoteWithPosition,
      });
      loadReviewPointViewedByUser({
        noteWithPosition: linkViewedbyUser.targetNoteWithPosition,
      });
    }
  }

  async function updateTextContentWithoutUndo(noteId, noteContentData) {
    const { updatedAt, ...data } = noteContentData;
    const res = await managedApi.restPatchMultiplePartForm(
      `text_content/${noteId}`,
      data,
    );
    store.commit('loadNotes', [res]);
    return res;
  }

  return {
    reviewMethods: {
      async getOneInitialReview() {
        const res = await managedApi.restGet(`reviews/initial`);
        loadReviewPointViewedByUser(res);
        return res;
      },

      async doInitialReview(data) {
        const res = await managedApi.restPost(`reviews`, data);
        loadReviewPointViewedByUser(res);
        return res;
      },

      async selfEvaluate(reviewPointId, data) {
        const res = await managedApi.restPost(
          `reviews/${reviewPointId}/self-evaluate`,
          data
        );
        loadReviewPointViewedByUser(res.reviewPointViewedByUser);
        return res;
      },

      async getNextReviewItem() {
        const res = await managedApi.restGet(`reviews/repeat`);
        loadReviewPointViewedByUser(res.reviewPointViewedByUser);
        return res;
      },
    },

    async getNoteWithDescendents(noteId) {
      const res = await managedApi.restGet(`notes/${noteId}/overview`);
      store.commit('loadNotes', res.notes);
      return res;
    },

    async getNoteAndItsChildren(noteId) {
      const res = await managedApi.restGet(`notes/${noteId}`);
      store.commit('loadNotes', res.notes);
      return res;
    },

    async getNotebooks() {
      const res = await managedApi.restGet(`notebooks`);
      store.commit('notebooks', res.notebooks);
      return res;
    },

    async createNotebook(circle, data) {
      const url = (() => {
        if (circle) {
          return `circles/${circle.id}/notebooks`;
        }
        return `notebooks/create`;
      })();

      const res = await managedApi.restPostMultiplePartForm(url, data);
      return res;
    },

    async createNote(parentId, data) {
      const res = await managedApi.restPostMultiplePartForm(
        `notes/${parentId}/create`,
        data
      );
      store.commit('loadNotes', res.notes);
      return res;
    },

    async createLink(sourceId, targetId, data) {
      const res = await managedApi.restPost(
        `links/create/${sourceId}/${targetId}`,
        data
      );
      store.commit('loadNotes', res.notes);
      return res;
    },

    async updateLink(linkId, data) {
      const res = await managedApi.restPost(`links/${linkId}`, data);
      store.commit('loadNotes', res.notes);
      return res;
    },

    async deleteLink(linkId) {
      const res = await managedApi.restPost(`links/${linkId}/delete`, {});
      store.commit('loadNotes', res.notes);
      return res;
    },

    async updateNote(noteId, noteContentData) {
      const { updatedAt, ...data } = noteContentData;
      const res = await managedApi.restPatchMultiplePartForm(`notes/${noteId}`, data);
      store.commit('loadNotes', [res]);
      return res;
    },

    async updateTextContent(noteId, noteContentData) {
      store.commit('addEditingToUndoHistory', { noteId });
      return updateTextContentWithoutUndo(noteId, noteContentData);
    },

    async addCommentToNote(noteId, commentContentData) {
      const { updatedAt, ...data } = commentContentData;
      const res = await managedApi.restPost(
        `comments/${noteId}/add`,
        data,
        () => null
      );
      store.commit('loadComments', [res]);
      return res;
    },

    async undo() {
      const history = store.getters.peekUndo();
      store.commit('popUndoHistory');
      if (history.type === 'editing') {
        return updateTextContentWithoutUndo(
          history.noteId,
          history.textContent
        );
      }
      const res = await managedApi.restPatch(
        `notes/${history.noteId}/undo-delete`,
        {}
      );
      store.commit('loadNotes', res.notes);
      if (res.notes[0].parentId === null) {
        this.getNotebooks(store);
      }
      return res;
    },

    async deleteNote(noteId) {
      const res = await managedApi.restPost(`notes/${noteId}/delete`, {}, () => null);
      store.commit('deleteNote', noteId);
      return res;
    },

    async getCurrentUserInfo() {
      const res = await managedApi.restGet(`user/current-user-info`);
      store.commit('currentUser', res.user);
      return res;
    },

    async updateUser(userId, data) {
      const res = await managedApi.restPatchMultiplePartForm(`user/${userId}`, data);
      store.commit('currentUser', res);
      return res;
    },

    async createUser(data) {
      const res = await managedApi.restPostMultiplePartForm(`user`, data);
      store.commit('currentUser', res);
      return res;
    },

    getFeatureToggle() {
      return (
        !window.location.href.includes('odd-e.com') &&
        managedApi.restGet(`testability/feature_toggle`).then((res) =>
          store.commit('featureToggle', res)
        )
      );
    },

    async setFeatureToggle(data) {
      const res = await managedApi.restPost(`testability/feature_toggle`, {
        enabled: data,
      });
      this.getFeatureToggle(store);
      return res;
    },

    getCircle(circleId) {
      return managedApi.restGet(`circles/${circleId}`);
    },
  };
};

export default storedApi;

