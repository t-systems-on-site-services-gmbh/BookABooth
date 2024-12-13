<template>
  <div>
    <h2 id="page-heading" data-cy="WaitingListHeading" class="mb-3">
      <span id="waiting-list-heading">Warteliste</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2 mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon><span>Liste aktualisieren</span>
        </button>
        <button class="btn btn-primary" :disabled="waitingListEntries.length === 0 || !isEnabled" @click="notifyWaitingList">
          <font-awesome-icon icon="envelope"></font-awesome-icon>
          <span>E-Mails senden</span>
        </button>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="filteredEntries.length === 0">
      <span>Keine wartenden Einträge gefunden.</span>
    </div>
    <div class="table-responsive" v-if="filteredEntries.length > 0">
      <table class="table table-striped" aria-describedby="waiting-list">
        <thead class="">
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>E-Mail</th>
            <th>Warteliste</th>
            <th>Aktionen</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="entry in filteredEntries" :key="entry.id">
            <td>{{ entry.id }}</td>
            <td>{{ entry.name }}</td>
            <td>{{ entry.mail }}</td>
            <td>
              <span :class="entry.waitingList ? 'text-success fw-bold' : 'text-danger fw-bold'">
                {{ entry.waitingList ? 'Ja' : 'Nein' }}
              </span>
            </td>
            <td>
              <div class="btn-group">
                <button
                  @click="toggleWaitingListStatus(entry)"
                  class="btn btn-primary btn-sm"
                  :class="entry.waitingList ? 'btn-danger' : 'btn-success'"
                >
                  <i class="fas" :class="entry.waitingList ? 'fa-times' : 'fa-check'"></i>
                  <span class="d-none d-md-inline">
                    {{ entry.waitingList ? 'Deaktivieren' : 'Aktivieren' }}
                  </span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./waitinglist.component.ts"></script>
