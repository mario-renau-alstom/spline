import { JoinComponent } from "../components/lineage/lineage-details/schema-details/join/join.component";
import { Type } from "@angular/core";
import { ProjectionComponent } from "../components/lineage/lineage-details/schema-details/projection/projection.component";
import { ExpressionComponent } from "../components/lineage/lineage-details/schema-details/expression/expression.component";

/*
 * Copyright 2019 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const enum OperationType {
    Projection = 'Projection',
    BatchRead = 'BatchRead',
    StreamRead = 'StreamRead',
    Join = 'Join',
    Union = 'Union',
    Generic = 'Generic',
    Filter = 'Filter',
    Sort = 'Sort',
    Aggregate = 'Aggregate',
    BatchWrite = 'BatchWrite',
    StreamWrite = 'StreamWrite',
    Alias = 'Alias'
}


export const ExpressionComponents: Map<string, Type<ExpressionComponent>> = new Map([
    [OperationType.Join, JoinComponent],
    [OperationType.Projection, ProjectionComponent]
])