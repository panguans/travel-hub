import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { createEntity, getEntity, reset, updateEntity } from './agency.reducer';

export const AgencyUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const agencyEntity = useAppSelector(state => state.agency.entity);
  const loading = useAppSelector(state => state.agency.loading);
  const updating = useAppSelector(state => state.agency.updating);
  const updateSuccess = useAppSelector(state => state.agency.updateSuccess);

  const handleClose = () => {
    navigate(`/agency${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.ratingAvg !== undefined && typeof values.ratingAvg !== 'number') {
      values.ratingAvg = Number(values.ratingAvg);
    }

    const entity = {
      ...agencyEntity,
      ...values,
      user_agency: users.find(it => it.id.toString() === values.user_agency?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...agencyEntity,
          user_agency: agencyEntity?.user_agency?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="travelHubApp.agency.home.createOrEditLabel" data-cy="AgencyCreateUpdateHeading">
            <Translate contentKey="travelHubApp.agency.home.createOrEditLabel">Create or edit a Agency</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="agency-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('travelHubApp.agency.description')}
                id="agency-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('travelHubApp.agency.address')}
                id="agency-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('travelHubApp.agency.website')}
                id="agency-website"
                name="website"
                data-cy="website"
                type="text"
              />
              <ValidatedField
                label={translate('travelHubApp.agency.ratingAvg')}
                id="agency-ratingAvg"
                name="ratingAvg"
                data-cy="ratingAvg"
                type="text"
              />
              <ValidatedField
                id="agency-user_agency"
                name="user_agency"
                data-cy="user_agency"
                label={translate('travelHubApp.agency.user_agency')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/agency" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AgencyUpdate;
