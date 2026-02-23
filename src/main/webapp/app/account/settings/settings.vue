<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-8 toastify-container">
        <h2 v-if="username" id="settings-title">
          <span>
            Profil von Benutzer [<strong>{{ username }}</strong
            >]
          </span>
        </h2>

        <div class="alert alert-success" role="alert" v-if="success">
          <strong>Die Einstellungen wurden gespeichert.</strong> Weiter geht es auf der
          <router-link :to="{ path: '/' }">Startseite</router-link>.
        </div>

        <div class="alert alert-danger" role="alert" v-if="errorEmailExists">
          <strong>E-Mail-Adresse wird bereits verwendet!</strong> Bitte wählen Sie eine andere aus.
        </div>

        <form name="form" id="settings-form" role="form" @submit.prevent="save()" v-if="settingsAccount" novalidate>
          <div class="accordion" id="accordionProfile">
            <div class="card" v-if="!hasAnyAuthority('ROLE_ADMIN')">
              <div class="card-header" Id="headingOne">
                <h3 class="mb-0">
                  <!--- <button class="btn btn-link btn-block text-left" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne"></button> -->
                  Unternehmensdaten
                </h3>
              </div>
              <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordionProfile">
                <div class="card-body">
                  <!--- Name des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.name">Firmenname</label>
                    <input
                      type="text"
                      class="form-control"
                      id="company.name"
                      name="company.name"
                      placeholder="Name Ihres Unternehmens"
                      :class="{ valid: !v$.settingsAccount.company.name.$invalid, invalid: v$.settingsAccount.company.name.$invalid }"
                      v-model="v$.settingsAccount.company.name.$model"
                      minlength="1"
                      required
                      data-cy="company.name"
                      @input="checkBillingAddress"
                    />
                    <div v-if="v$.settingsAccount.company.name.$anyDirty && v$.settingsAccount.company.name.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.required"
                        >Ein Firmenname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.minLength"
                        >Ihr Firmenname muss mindestens 1 Zeichen lang sein.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.maxLength"
                        >Ihr Firmenname darf nicht länger als 100 Zeichen sein.</small
                      >
                    </div>
                  </div>
                  <!--- Kurzbeschreibung des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.description"
                      >Kurze Beschreibung Ihres Unternehmens (max. 1024 Zeichen)</label
                    >
                    <textarea
                      id="company.description"
                      name="company.description"
                      class="form-control"
                      placeholder="Beschreibung Ihres Unternehmens"
                      :class="{
                        valid: !v$.settingsAccount.company.description.$invalid,
                        invalid: v$.settingsAccount.company.description.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.description.$model"
                      minlength="0"
                      required
                      data-cy="company.description"
                      rows="5"
                    >
                    </textarea>
                    <div v-if="v$.settingsAccount.company.description.$anyDirty && v$.settingsAccount.company.description.$invalid">
                      <small class="form-text text-danger" v-if="v$.settingsAccount.company.description.maxLength"
                        >Ihre Firmenbeschreibung darf nicht länger als 1024 Zeichen sein.</small
                      >
                    </div>
                  </div>
                  <!--- Unternehmenslogo Previewfenster -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.logo">Unternehmenslogo (max. 10 MB)</label>
                    <br />
                    <img
                      v-if="settingsAccount.company?.logo"
                      id="company.logo"
                      alt="Unternehmenslogo"
                      :src="absoluteImageUrl"
                      style="width: 150px; height: auto"
                    />
                  </div>
                  <!--- Unternehmenslogo hochladen / aktualisieren -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.logoUpload">
                      <p v-if="settingsAccount.company?.logo">Aktualisieren Sie hier Ihr Unternehmenslogo</p>
                      <p v-else>Laden Sie hier Ihr Unternehmenslogo hoch</p>
                    </label>
                    <input
                      type="file"
                      ref="file"
                      class="form-control-file"
                      id="company.logoUpload"
                      name="company.logoUpload"
                      :class="{
                        valid: !v$.settingsAccount.company.logoUpload.$invalid,
                        invalid: v$.settingsAccount.company.logoUpload.$invalid,
                      }"
                      data-cy="company.logoupload"
                      accept="image/*"
                      @change="logoUpload"
                    />
                  </div>
                  <!--- Adresse des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label">Rechnungsanschrift</label>
                    <div v-if="billingAddressWarning" class="alert alert-warning" id="warningBillingAddress">
                      <font-awesome-icon icon="triangle-exclamation" />
                      <span
                        >Hinweis: Der Firmenname <i>{{ v$.settingsAccount.company.name.$model }}</i> ist aktuell nicht in der
                        Rechnungsanschrift enthalten. Sind Sie sich sicher?</span
                      >
                    </div>
                    <div class="row">
                      <div class="col-md-12">
                        <label class="form-control-label" for="company.billingAddressRow1">Adresszeile 1</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingAddressRow1"
                          name="company.billingAddressRow1"
                          placeholder="Firmenname"
                          :class="{
                            valid: !v$.settingsAccount.company.billingAddressRow1.$invalid,
                            invalid: v$.settingsAccount.company.billingAddressRow1.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingAddressRow1.$model"
                          minlength="1"
                          required
                          data-cy="company.billingaddressrow1"
                          @input="checkBillingAddress"
                        />
                        <div
                          v-if="
                            v$.settingsAccount.company.billingAddressRow1.$anyDirty &&
                            v$.settingsAccount.company.billingAddressRow1.$invalid
                          "
                        >
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingAddressRow1.required"
                            >Adresszeile 1 wird benötigt.</small
                          >
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingAddressRow1.minLength"
                            >Adresszeile 1 muss mindestens 1 Zeichen lang sein.</small
                          >
                        </div>
                      </div>
                    </div>
                    <div class="row mt-2">
                      <div class="col-md-12">
                        <label class="form-control-label" for="company.billingAddressRow2">Adresszeile 2</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingAddressRow2"
                          name="company.billingAddressRow2"
                          placeholder="Zusatzinformationen (z.B. Abteilung, etc.)"
                          :class="{
                            valid: !v$.settingsAccount.company.billingAddressRow2.$invalid,
                            invalid: v$.settingsAccount.company.billingAddressRow2.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingAddressRow2.$model"
                          data-cy="company.billingaddressrow2"
                        />
                      </div>
                    </div>
                    <div class="row mt-2">
                      <div class="col-md-12">
                        <label class="form-control-label" for="company.billingAddressRow3">Adresszeile 3</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingAddressRow3"
                          name="company.billingAddressRow3"
                          placeholder="Zusatzinformationen"
                          :class="{
                            valid: !v$.settingsAccount.company.billingAddressRow3.$invalid,
                            invalid: v$.settingsAccount.company.billingAddressRow3.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingAddressRow3.$model"
                          data-cy="company.billingaddressrow3"
                        />
                      </div>
                    </div>
                    <div class="row mt-2">
                      <div class="col-md-12">
                        <label class="form-control-label" for="company.billingAddressRow4">Adresse Zeile 4</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingAddressRow4"
                          name="company.billingAddressRow4"
                          placeholder="z.B. Straße und Hausnummer oder Postfach"
                          :class="{
                            valid: !v$.settingsAccount.company.billingAddressRow4.$invalid,
                            invalid: v$.settingsAccount.company.billingAddressRow4.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingAddressRow4.$model"
                          data-cy="company.billingaddressrow4"
                        />
                      </div>
                    </div>
                    <div class="row mt-2">
                      <div class="col-md-6">
                        <label class="form-control-label" for="company.billingZipCode">PLZ</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingZipCode"
                          name="company.billingZipCode"
                          placeholder="PLZ"
                          :class="{
                            valid: !v$.settingsAccount.company.billingZipCode.$invalid,
                            invalid: v$.settingsAccount.company.billingZipCode.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingZipCode.$model"
                          minlength="1"
                          required
                          data-cy="company.billingzipcode"
                        />
                        <div
                          v-if="v$.settingsAccount.company.billingZipCode.$anyDirty && v$.settingsAccount.company.billingZipCode.$invalid"
                        >
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingZipCode.required"
                            >PLZ wird benötigt.</small
                          >
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingZipCode.minLength"
                            >PLZ muss mindestens 1 Zeichen lang sein.</small
                          >
                        </div>
                      </div>
                      <div class="col-md-6">
                        <label class="form-control-label" for="company.billingCity">Ort</label>
                        <input
                          type="text"
                          class="form-control"
                          id="company.billingCity"
                          name="company.billingCity"
                          placeholder="Ort"
                          :class="{
                            valid: !v$.settingsAccount.company.billingCity.$invalid,
                            invalid: v$.settingsAccount.company.billingCity.$invalid,
                          }"
                          v-model="v$.settingsAccount.company.billingCity.$model"
                          minlength="1"
                          required
                          data-cy="company.billingcity"
                        />
                        <div v-if="v$.settingsAccount.company.billingCity.$anyDirty && v$.settingsAccount.company.billingCity.$invalid">
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingCity.required"
                            >Ort wird benötigt.</small
                          >
                          <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingCity.minLength"
                            >Ort muss mindestens 1 Zeichen lang sein.</small
                          >
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- Bemerkung -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.comment">Bemerkung, wird für Rechnung übernommen</label>
                    <textarea
                      id="company.comment"
                      name="company.comment"
                      class="form-control"
                      placeholder="Bemerkung"
                      :class="{
                        valid: !v$.settingsAccount.company.comment.$invalid,
                        invalid: v$.settingsAccount.company.comment.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.comment.$model"
                      minlength="0"
                      data-cy="company.bemerkung"
                    >
                    </textarea>
                    <div v-if="v$.settingsAccount.company.comment.$invalid">
                      <small class="form-text text-danger" v-if="v$.settingsAccount.company.comment.maxLength">
                        Die Bemerkung darf nicht länger als 1024 Zeichen sein.
                      </small>
                    </div>
                  </div>
                  <!--- Checkbox um Freigabe in Ausstellerliste zu widerrufen -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.exhibitorList">Freigabe Ausstellerliste </label>
                    <br />
                    <input
                      type="checkbox"
                      id="company.exhibitorList"
                      name="company.exhibitorList"
                      :class="{
                        valid: !v$.settingsAccount.company.exhibitorList.$invalid,
                        invalid: v$.settingsAccount.company.exhibitorList.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.exhibitorList.$model"
                      data-cy="exhibitorlist"
                    />
                    Hiermit bestägtige ich, dass mein Unternehmen öffentlich auf der Ausstellerliste und der Standauswahl während des
                    Buchungsprozesses erscheinen darf. Dritte sehen dadurch den Firmennamen, die Firmenbeschreibung, das Logo und den
                    gebuchten Stand. Wurde noch keine Buchung getätigt oder eine bestehende Buchung storniert, können diese Informationen
                    nicht eingesehen werden. Eine Standbuchung kann auch ohne die <i>Freigabe Ausstellerliste</i> erfolgen.
                  </div>
                </div>
              </div>
            </div>
            <div class="card">
              <div class="card-header" id="headingTwo">
                <h3 class="mb-0">
                  <!--- <button class="btn btn-link btn-block text-left" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo"></button> -->
                  Benutzerdaten
                </h3>
              </div>
              <div id="collapseTwo" class="collapse show" aria-labelledby="headingTwo" data-parent="#accordionProfile">
                <div class="card-body">
                  <!--- Vorname des Ansprechpartners / Accountbesitzers -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.firstName">Vorname Ansprechpartner</label>
                    <input
                      type="text"
                      class="form-control"
                      id="user.firstName"
                      name="user.firstName"
                      placeholder="Ihr Vorname"
                      :class="{ valid: !v$.settingsAccount.user.firstName.$invalid, invalid: v$.settingsAccount.user.firstName.$invalid }"
                      v-model="v$.settingsAccount.user.firstName.$model"
                      minlength="1"
                      maxlength="50"
                      required
                      data-cy="user.firstname"
                    />
                    <div v-if="v$.settingsAccount.user.firstName.$anyDirty && v$.settingsAccount.user.firstName.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.required"
                        >Ihr Vorname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.minLength"
                        >Ihr Vorname muss mindestens 1 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.maxLength"
                        >Ihr Vorname darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                  <!--- Nachname des Ansprechpartners / Accountbesitzers -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.lastName">Nachname Ansprechpartner</label>
                    <input
                      type="text"
                      class="form-control"
                      id="user.lastName"
                      name="user.lastName"
                      placeholder="Ihr Nachname"
                      :class="{ valid: !v$.settingsAccount.user.lastName.$invalid, invalid: v$.settingsAccount.user.lastName.$invalid }"
                      v-model="v$.settingsAccount.user.lastName.$model"
                      minlength="1"
                      maxlength="50"
                      required
                      data-cy="user.lastname"
                    />
                    <div v-if="v$.settingsAccount.user.lastName.$anyDirty && v$.settingsAccount.user.lastName.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.required"
                        >Ihr Nachname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.minLength"
                        >Ihr Nachname muss mindestens 1 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.maxLength"
                        >Ihr Nachname darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                  <!--- E-Mail-Adresse des Ansprechpartners -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.email">E-Mail-Adresse</label>
                    <input
                      type="email"
                      class="form-control"
                      id="user.email"
                      name="user.email"
                      placeholder="Ihre E-Mail-Adresse"
                      :class="{ valid: !v$.settingsAccount.user.email.$invalid, invalid: v$.settingsAccount.user.email.$invalid }"
                      v-model="v$.settingsAccount.user.email.$model"
                      minlength="5"
                      maxlength="254"
                      email
                      required
                      data-cy="user.email"
                    />
                    <div v-if="v$.settingsAccount.user.email.$anyDirty && v$.settingsAccount.user.email.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.required"
                        >Ihre E-Mail-Adresse wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.email"
                        >Ihre E-Mail-Adresse ist ungültig.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.minLength">
                        Ihre E-Mail-Adresse muss mindestens 5 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.maxLength"
                        >Ihre E-Mail-Adresse darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                  <!--- Telefonnummer des Ansprechpartners -->
                  <div class="form-group" v-if="!hasAnyAuthority('ROLE_ADMIN')">
                    <label class="form-control-label" for="phoneNumber">Telefonnummer Ansprechpartner</label>
                    <input
                      type="phone"
                      class="form-control"
                      id="phoneNumber"
                      name="phoneNumber"
                      placeholder="Ihre Telefonnummer"
                      :class="{ valid: !v$.settingsAccount.phoneNumber.$invalid, invalid: v$.settingsAccount.phoneNumber.$invalid }"
                      v-model="v$.settingsAccount.phoneNumber.$model"
                      maxlength="20"
                      required
                      data-cy="phonenumber"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <br />
          <button
            type="submit"
            :disabled="
              v$.settingsAccount.user.email.$invalid ||
              v$.settingsAccount.user.firstName.$invalid ||
              v$.settingsAccount.user.lastName.$invalid
            "
            class="btn btn-primary"
            data-cy="submit"
          >
            Speichern
          </button>
        </form>
        <br />
        <div class="form-group" v-if="!hasAnyAuthority('ROLE_ADMIN')">
          <!--- Aktueller Buchungsstaus -->
          <div>
            <p v-if="settingsAccount?.booking?.status === 'CONFIRMED'">Sie haben eine <strong>bestätigte</strong> Buchung.</p>
            <p v-else>Es liegt noch <strong>keine</strong> Buchung vor.</p>
          </div>
          <!--- Buchung stornieren -->
          <p v-if="settingsAccount?.booking?.status === 'CONFIRMED'">
            Falls Sie Ihre aktive Buchung stornieren möchten, können Sie dies hier tun.
          </p>
          <p v-else>Sie haben im Moment keine aktive Standbuchung.</p>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="
              settingsAccount?.booking?.status == 'CANCELED' ||
              settingsAccount?.booking?.status == null ||
              settingsAccount?.booking?.status == 'BLOCKED'
            "
            @click="showCancelBooking"
            data-cy="cancel"
          >
            Standbuchung stornieren
          </button>

          <br /><br />
          <!--- Von Warteliste entfernen -->
          <p v-if="settingsAccount?.company?.waitingList">
            Sie befinden sich aktuell auf der Warteliste. Wenn Sie sich davon entfernen möchten, nutzen Sie den folgenden Button.
          </p>
          <p v-else>
            Sie befinden sich im Moment nicht auf der Warteliste.<br />
            Wenn alle Stände gebucht sind, können Sie sich unter "Buchungsstatus" auf der Startseite eintragen.
          </p>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="!settingsAccount?.company?.waitingList"
            @click="removeWaitingList"
            data-cy="remove"
          >
            Von Warteliste entfernen
          </button>

          <b-modal ref="cancelBooking-modal" hide-footer title="Standbuchung stornieren">
            <div class="d-block text-left">
              <h3>Sind Sie sicher, dass Sie Ihre Standbuchung stornieren wollen?</h3>
              <p>Wenn Sie fortfahren, kann Ihr gewählter Stand von anderen Unternehmen gebucht werden.</p>
              <p>
                Es fallen Stornogebühren in Höhe von {{ 100 - system.cancellationReimbursement }}% des Buchungspreises an. Bei einer
                Stornierung nach dem {{ formatDate(system.cancellationReimbursementUntil) }} wird der gesamte Betrag fällig.
                <br />
                Ihnen entstehen <strong>{{ formatCurrency(calculateCancellationFee()) }}</strong> Stornogebühren.
              </p>
              <p>Ihr Benutzerkonto bleibt bestehen und Sie haben die Möglichkeit, andere freie Stände zu buchen.</p>
            </div>
            <div class="d-flex justify-content-end">
              <b-button class="btn btn-secondary" @click="hideCancelBooking">Abbrechen</b-button>
              <b-button type="button" class="btn btn-danger ml-3" @click="setCanceled">Kostenpflichtig stornieren</b-button>
            </div>
          </b-modal>
          <br /><br />
        </div>
        <!--- Konto löschen + Modal -->
        <p v-if="settingsAccount?.booking?.status === 'CONFIRMED'">
          Sie habe eine bestätigte Standbuchung und können daher Ihr Konto nicht löschen.
        </p>
        <p v-else-if="settingsAccount?.booking?.status === 'CANCELED'">
          Sie habe eine stornierte Standbuchung und können daher Ihr Konto erst nach Rechnungsstellung löschen.
        </p>
        <p v-else-if="hasAnyAuthority('ROLE_ADMIN') && onlyOneAdmin">
          Sie sind der einzige Admin im System. Es ist nicht möglich, den einzigen Admin zu löschen.
        </p>
        <p v-else>Hier können Sie Ihr Konto löschen. Wenn Sie Ihr Konto löschen, kann es nicht wiederhergestellt werden.</p>
        <button
          type="button"
          class="btn btn-danger"
          id="show-btn"
          :disabled="
            settingsAccount?.booking?.status === 'CANCELED' ||
            settingsAccount?.booking?.status === 'CONFIRMED' ||
            (hasAnyAuthority('ROLE_ADMIN') && onlyOneAdmin)
          "
          @click="showDeleteModal"
        >
          Konto löschen
        </button>
        <b-modal ref="deleteAcc-modal" hide-footer title="Benutzerkonto löschen" @hidden="resetDeleteModal">
          <div class="d-block text-left">
            <div class="w-100">
              <b-alert show data-cy="deleteError" variant="danger" v-if="deleteError"
                ><strong>Ungültiges Passwort!</strong>
                Bitte überprüfen Sie Ihr Passwort und versuchen Sie es erneut.
              </b-alert>
            </div>
            <h3>Sind Sie sich sicher, dass Sie Ihr Konto löschen wollen?</h3>
            <p>Beachten Sie, dass dies nicht rückgängig gemacht werden kann.</p>
            <form name="deleteForm" id="delete-form" @submit.prevent="confirmDelete(settingsAccount.user.id)">
              <b-form-group label="Bestätigen Sie mit Ihrem Passwort." label-for="passwordConfirm">
                <b-form-input
                  id="passwordConfirm"
                  type="password"
                  name="passwordConfirm"
                  :class="{ valid: !v$.deleteAccount.passwordConfirm.$invalid, invalid: v$.deleteAccount.passwordConfirm.$invalid }"
                  placeholder="Ihr Passwort"
                  v-model="passwordConfirm"
                  data-cy="passwordconfirm"
                >
                </b-form-input>
              </b-form-group>
            </form>
          </div>
          <div class="d-flex justify-content-end">
            <b-button class="btn btn-secondary" @click="hideDeleteModal">Abbrechen</b-button>
            <b-button type="submit" class="btn btn-danger ml-3" id="confirmDelete" @click="confirmDelete(settingsAccount.user.id)"
              >Konto löschen</b-button
            >
          </div>
        </b-modal>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./settings.component.ts"></script>
