/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

import java.util.List;
import java.util.Map;

/**
 * Created by Hannes on 02.04.2017.
 */
public interface RoleSet {
    Map<String, List<String>> readRoleSet(String verb);
}
