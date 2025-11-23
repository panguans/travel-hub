import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agency.reducer';

export const AgencyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agencyEntity = useAppSelector(state => state.agency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agencyDetailsHeading">
          <Translate contentKey="travelHubApp.agency.detail.title">Agency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{agencyEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="travelHubApp.agency.description">Description</Translate>
            </span>
          </dt>
          <dd>{agencyEntity.description}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="travelHubApp.agency.address">Address</Translate>
            </span>
          </dt>
          <dd>{agencyEntity.address}</dd>
          <dt>
            <span id="website">
              <Translate contentKey="travelHubApp.agency.website">Website</Translate>
            </span>
          </dt>
          <dd>{agencyEntity.website}</dd>
          <dt>
            <span id="ratingAvg">
              <Translate contentKey="travelHubApp.agency.ratingAvg">Rating Avg</Translate>
            </span>
          </dt>
          <dd>{agencyEntity.ratingAvg}</dd>
          <dt>
            <Translate contentKey="travelHubApp.agency.user_agency">User Agency</Translate>
          </dt>
          <dd>{agencyEntity.user_agency ? agencyEntity.user_agency.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/agency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/agency/${agencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgencyDetail;
