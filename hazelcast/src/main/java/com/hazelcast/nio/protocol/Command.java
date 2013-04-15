/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.nio.protocol;

public enum Command {
    AUTH(), OK(), ERROR(), INSTANCES(), MEMBERS(), MEMBERLISTEN(), CLUSTERTIME(), PARTITIONS(), MIGRATIONLISTEN(),
    TRXCOMMIT(), TRXROLLBACK(), TRXBEGIN(), TRXSTATUS(),
    DESTROY(), UNKNOWN(), EVENT(),

    MGET(), MGETALL(), MPUT(), MTRYPUT(), MSET(), MPUTTRANSIENT(), MPUTANDUNLOCK(), MREMOVE(), MREMOVEITEM(),
    MCONTAINSKEY(), MCONTAINSVALUE(), ADDLISTENER(), MEVENT(), REMOVELISTENER(), KEYSET(), MENTRYSET(), MGETENTRYVIEW(),
    MLOCK(), MISLOCKED(), MUNLOCK(), MTRYLOCK(), MLOCKMAP(), MUNLOCKMAP(), MFORCEUNLOCK(), MPUTALL(), MPUTIFABSENT(),
    MREMOVEIFSAME(), MREPLACEIFNOTNULL(), MREPLACEIFSAME(), MTRYLOCKANDGET(), MTRYREMOVE(), MFLUSH(), MEVICT(),
    MDELETE(), MEXECUTEONKEY(), MEXECUTEONALLKEYS(),
    MLISTEN(), MREMOVELISTENER(), MSIZE(), MADDINDEX(), MISKEYLOCKED(), MADDINTERCEPTOR(), MREMOVEINTERCEPTOR(),

    QOFFER(), QPUT(), QPOLL(), QTAKE(), QSIZE(), QPEEK(), QREMOVE(), QREMCAPACITY(), QENTRIES(), QLISTEN(),
    QREMOVELISTENER(), QEVENT(),

    CDLAWAIT(), CDLGETCOUNT(), CDLSETCOUNT(), CDLCOUNTDOWN(),

    ALADDANDGET(), ALGETANDADD(), ALGETANDSET(), ALCOMPAREANDSET(),
    SEMATTACHDETACHPERMITS(), SEMCANCELACQUIRE(), SEMDESTROY(), SEM_DRAIN_PERMITS(), SEMGETATTACHEDPERMITS(),
    SEMGETAVAILPERMITS(), SEMREDUCEPERMITS(), SEMRELEASE(), SEMTRYACQUIRE(),

    LOCK(), TRYLOCK(), UNLOCK(), FORCEUNLOCK(), ISLOCKED(),

    SADD(), SREMOVE(), SCONTAINS(), SGETALL(), SLISTEN(), SSIZE(),
    LADD(), LREMOVE(), LCONTAINS(), LGETALL(), LLISTEN(), LGET(), LINDEXOF(), LLASTINDEXOF(), LSET(), LSIZE(),
    MMPUT(), MMREMOVE(), MMVALUECOUNT(), MMSIZE(), MMCONTAINSENTRY(), MMCONTAINSKEY(), MMCONTAINSVALUE(), MMKEYS(),
    MMGET(), MMLOCK(), MMUNLOCK(), MMTRYLOCK(), MMLISTEN(),
    ADDANDGET(), GETANDSET(), COMPAREANDSET(), GETANDADD(),
    NEWID(), INITID(),
    TPUBLISH(), TLISTEN(), TREMOVELISTENER(), MESSAGE(),
    EXECUTE(),
    PING();

    private final byte value;

    static byte idGen = 0;
    public final static int LENGTH = 200;

    private Command() {
        this.value = nextId();
    }

    public byte getValue() {
        return value;
    }

    private synchronized byte nextId() {
        return idGen++;
    }
}